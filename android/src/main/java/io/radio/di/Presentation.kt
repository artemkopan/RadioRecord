package io.radio.di

//import io.radio.shared.presentation.player.PlayerViewModelOld
//import io.radio.shared.presentation.podcast.details.PodcastDetailsViewModelOld
import io.radio.shared.presentation.podcast.PodcastsViewModel
import io.radio.shared.presentation.stations.StationsViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

//    viewModel { (params: SavedStateHandle) ->
//        PodcastDetailsViewModelOld(
//            params,
//            get(),
//            get(),
//            get(),
//            get(),
//            get(),
//            get()
//        )
//    }

    viewModel { PodcastsViewModel(get(), get(), get()) }
    viewModel { StationsViewModel(get(), get(), get()) }
//    viewModel { PlayerViewModelOld(get(), get(), get(), get(), get(), get(), get()) }


}