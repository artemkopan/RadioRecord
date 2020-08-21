package io.shared.store.playlist

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val playlistModule = DI.Module("playlist") {

    bind() from provider { PlaylistObserveTracksStateMiddleware(instance(), instance()) }
    bind() from provider { PlaylistStoreFactory(instance()) }
}