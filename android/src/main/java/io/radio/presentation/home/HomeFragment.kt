package io.radio.presentation.home

import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import io.radio.R
import io.radio.shared.base.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.include_home_header.*

class HomeFragment : BaseFragment(R.layout.fragment_home), HomePagerContentScroller {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
    }

    override fun onScrolled(fraction: Float) {
        pagerElevation.isSelected = fraction == 1f
    }

}