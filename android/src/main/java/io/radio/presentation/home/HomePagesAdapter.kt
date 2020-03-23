package io.radio.presentation.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.radio.presentation.podcast.PodcastsFragment
import io.radio.presentation.stations.StationsFragment

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