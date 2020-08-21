package io.shared.presentation

import io.shared.mvi.StateStorage
import io.shared.presentation.player.PlayerViewBinder
import io.shared.presentation.podcast.details.PodcastDetailsParams
import io.shared.presentation.podcast.details.PodcastDetailsViewBinder
import io.shared.presentation.podcast.home.PodcastViewBinder
import io.shared.presentation.stations.StationViewBinder
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance

val presentationModule = DI.Module("presentation") {

    bind() from factory { state: StateStorage -> StationViewBinder(state, instance(), instance()) }
    bind() from factory { state: StateStorage ->
        PodcastViewBinder(
            state,
            instance(),
            instance(),
            instance(),
            instance()
        )
    }

    bind() from factory { state: StateStorage ->
        val params = state.get<PodcastDetailsParams>("params")!!

        PodcastDetailsViewBinder(
            state,
            instance(arg = params.id),
            instance(),
            instance(),
            instance()
        )
    }

    bind() from factory { state: StateStorage ->
        PlayerViewBinder(
            state,
            instance(),
            instance(),
            instance()
        )
    }

}