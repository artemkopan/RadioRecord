package io.radio.feature.home

import android.os.Bundle
import android.view.View
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter.FragmentTransactionCallback.OnPostEventListener
import androidx.viewpager2.widget.ViewPager2
import io.radio.R
import io.radio.feature.podcast.PodcastsSharedElementSupport
import io.radio.shared.base.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_podcasts.*
import kotlinx.android.synthetic.main.include_home_header.*

class HomeFragment : BaseFragment(R.layout.fragment_home), HomePagerContentScroller,
    PodcastsSharedElementSupport {

    private var selectedPos = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                if (selectedPos < 0 || names == null || sharedElements == null) return

                val selectedHolder =
                    radioPodcastRecycleView?.findViewHolderForAdapterPosition(selectedPos)
                if (selectedHolder?.itemView == null) {
                    return
                }

                sharedElements[names.first()] =
                    selectedHolder.itemView.findViewById(R.id.stationPreviewImage)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        headerLayout.setTransition(R.id.stationsTitle, R.id.podcastsTitle)

        val pagesAdapter = HomePagesAdapter(this)
        pagerView.adapter = pagesAdapter
        pagerView.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                headerLayout.progress = ((position + positionOffset) / (pagesAdapter.itemCount - 1))
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
                        pagerView.post { startPostponedEnterTransition() }
                    }
                }
            }
        })
    }

    override fun onScrolled(fraction: Float) {
        pagerElevation.isSelected = fraction == 1f
    }

    override fun setSelectedPos(pos: Int) {
        selectedPos = pos
    }

}