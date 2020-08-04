package io.radio.shared.store.playlist

import org.koin.dsl.module

val playlistModule = module {

    factory { PlaylistObserveTracksStateMiddleware(get(), get()) }
    factory { PlaylistStoreFactory(get()) }
}