package io.radio.presentation.home

import android.os.Bundle
import android.view.View
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter.FragmentTransactionCallback.OnPostEventListener
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import io.radio.R
import io.radio.base.BaseFragment
import io.radio.databinding.FragmentHomeBinding
import io.radio.presentation.podcast.home.PodcastsSharedElementSupport

class HomeFragment : BaseFragment(R.layout.fragment_home),
    HomePagerContentScroller,
    PodcastsSharedElementSupport {

    private var selectedPos = -1

    private val binding: FragmentHomeBinding by viewBinding { fragment ->
        FragmentHomeBinding.bind(fragment.requireView())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                if (selectedPos < 0 || names == null || sharedElements == null) return

                val selectedHolder =
                    view?.findViewById<RecyclerView>(R.id.radioPodcastRecycleView)
                        ?.findViewHolderForAdapterPosition(selectedPos)
                if (selectedHolder?.itemView == null) {
                    return
                }

                names.firstOrNull()?.let {
                    sharedElements[it] =
                        selectedHolder.itemView.findViewById(R.id.stationPreviewImage)
                }
            }
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        binding.headerLayout.root.setTransition(R.id.stationsTitle, R.id.podcastsTitle)

        val pagesAdapter = HomePagesAdapter(this)
        binding.pagerView.adapter = pagesAdapter
        binding.pagerView.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                binding.headerLayout.root.progress = ((position + positionOffset) / (pagesAdapter.itemCount - 1))
            }
        })

        pagesAdapter.registerFragmentTransactionCallback(object :
            FragmentStateAdapter.FragmentTransactionCallback() {
            override fun onFragmentMaxLifecyclePreUpdated(
                fragment: Fragment,
                maxLifecycleState: Lifecycle.State
            ): OnPostEventListener {
                return OnPostEventListener {
                    if (maxLifecycleState.isAtLeast(Lifecycle.State.RESUMED)) {
                        binding.pagerView.post { startPostponedEnterTransition() }
                    }
                }
            }
        })
    }

    override fun onScrolled(fraction: Float) {
        binding.pagerElevation.isSelected = fraction == 1f
    }

    override fun setSelectedPos(pos: Int) {
        selectedPos = pos
    }

}