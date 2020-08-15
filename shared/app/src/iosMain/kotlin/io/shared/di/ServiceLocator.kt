package io.shared.di

import io.shared.configs.SystemConfigImpl
import io.shared.core.IoDispatcher
import io.shared.core.Logger
import io.shared.core.MainDispatcher
import io.shared.formatters.ErrorFormatter
import io.shared.mapper.*
import io.shared.mvi.BinderDisposable
import io.shared.mvi.StateStorage
import io.shared.network.HttpClientProviderImpl
import io.shared.network.HttpEngineProvider
import io.shared.network.RadioApiSourceImpl
import io.shared.presentation.stations.StationView
import io.shared.presentation.stations.StationViewBinder
import io.shared.repo.RadioRepositoryImpl
import io.shared.store.player.MediaPlayer
import io.shared.store.stations.LoadStationMiddleware
import io.shared.store.stations.PlayStationMiddleware
import io.shared.store.stations.StationStore
import io.shared.store.stations.StationStoreFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ServiceLocator {
//
//    private val koin = startKoin {
//        logger(KoinLogger())
//        modules(commonModules)
//    }.koin

    fun getStationViewBinder(): StationViewBinder {
//        return koin.get(parameters = { parametersOf(StateStorage()) })
        val radioApiSource = RadioApiSourceImpl(
            HttpClientProviderImpl(SystemConfigImpl(), HttpEngineProvider()),
            RadioStationMapper(),
            RadioPodcastMapper(),
            RadioPodcastDetailsMapper(RadioPodcastDetailsItemMapper())
        )
        val radioRepository = RadioRepositoryImpl(radioApiSource)

        return StationViewBinder(
            StateStorage(), StationStoreFactory(
                LoadStationMiddleware(radioRepository),
                PlayStationMiddleware(MediaPlayer(), TrackItemFromRadioStationMapper())
            ), ErrorFormatter()
        )
    }

    fun bindStationViewBinder(render: (StationView.Model) -> Unit): BinderDisposable {
//        return koin.get(parameters = { parametersOf(StateStorage()) })
        val radioApiSource = RadioApiSourceImpl(
            HttpClientProviderImpl(SystemConfigImpl(), HttpEngineProvider()),
            RadioStationMapper(),
            RadioPodcastMapper(),
            RadioPodcastDetailsMapper(RadioPodcastDetailsItemMapper())
        )
        val radioRepository = RadioRepositoryImpl(radioApiSource)

        return StationViewBinder(
            StateStorage(), StationStoreFactory(
                LoadStationMiddleware(radioRepository),
                PlayStationMiddleware(MediaPlayer(), TrackItemFromRadioStationMapper())
            ), ErrorFormatter()
        ).bindDisposable(object : StationView {
            override val intents: Flow<StationView.Intent>
                get() = emptyFlow()

            override fun render(model: StationView.Model) {
                render(model)
            }

            override fun acceptEffect(effect: StationView.Effect) {

            }
        })
    }

    fun getStations(state: (StationStore.State) -> Unit) {

        val radioApiSource = RadioApiSourceImpl(
            HttpClientProviderImpl(SystemConfigImpl(), HttpEngineProvider()),
            RadioStationMapper(),
            RadioPodcastMapper(),
            RadioPodcastDetailsMapper(RadioPodcastDetailsItemMapper())
        )
        val radioRepository = RadioRepositoryImpl(radioApiSource)

        val factory = StationStoreFactory(
            LoadStationMiddleware(radioRepository),
            PlayStationMiddleware(MediaPlayer(), TrackItemFromRadioStationMapper())
        )

        GlobalScope.launch(MainDispatcher) {
            factory.create(this, StateStorage()).stateFlow
                .onEach {
                    state(it)
                    Logger.d(message = "State: $it")
                }
                .launchIn(this)
        }
    }

}