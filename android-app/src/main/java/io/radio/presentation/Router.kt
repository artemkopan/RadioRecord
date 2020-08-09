@file:Suppress("NOTHING_TO_INLINE")

package io.radio.presentation

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import io.radio.R
import io.radio.base.BaseFragment
import io.radio.presentation.podcast.details.PodcastDetailsFragmentArgs
import io.radio.presentation.podcast.home.PodcastsFragment
import io.radio.presentation.stations.StationsFragment
import io.shared.presentation.podcast.details.PodcastDetailsParams

inline fun PodcastsFragment.routeDetails(params: PodcastDetailsParams, extras: Navigator.Extras?) {
    findNavController().navigate(
        R.id.action_stationsFragment_to_podcastDetailsFragment,
        PodcastDetailsFragmentArgs(params).toBundle(),
        null,
        extras
    )
}

inline fun StationsFragment.routeDetails(params: PodcastDetailsParams, extras: Navigator.Extras?) {
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


inline fun Context.createPlayerPendingIntent(): PendingIntent {
//    return NavDeepLinkBuilder(this)
//        .setComponentName(MainActivity::class.java)
//        .setGraph(R.navigation.main_graph)
//        .setDestination(R.id.playerFragment)
//        .createPendingIntent()
    val intent = Intent(this, MainActivity::class.java)
    return PendingIntent.getActivity(this, 12, intent, FLAG_ONE_SHOT)
}