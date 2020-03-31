@file:Suppress("NOTHING_TO_INLINE")

package io.radio.presentation

import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import io.radio.R
import io.radio.presentation.podcast.PodcastsFragment
import io.radio.presentation.podcast.details.PodcastDetailsFragmentArgs
import io.radio.shared.base.fragment.BaseFragment
import io.radio.shared.presentation.podcast.details.PodcastDetailsParams

inline fun PodcastsFragment.routeDetails(params: PodcastDetailsParams, extras: Navigator.Extras?) {
    findNavController().navigate(
        R.id.action_stationsFragment_to_podcastDetailsFragment,
        PodcastDetailsFragmentArgs(params).toBundle(),
        null,
        extras
    )
}

inline fun BaseFragment.routePlayer() {
    findNavController().navigate(R.id.playerFragment)
}