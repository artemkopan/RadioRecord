package io.radio.feature.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.radio.feature.podcast.PodcastsFragment
import io.radio.feature.stations.StationsFragment

class HomePagesAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StationsFragment()
            1 -> PodcastsFragment()
            else -> throw NotImplementedError()
        }
    }

}