package io.radio.shared.feature.player

import io.radio.shared.base.mvi.StoreBuilder
import io.radio.shared.base.mvi.v3.Store
import io.radio.shared.base.viewmodel.StateStorage
import io.radio.shared.base.viewmodel.ViewModel

class PlayerViewModel(
    private val stateStorage: StateStorage,
    private val playerReducer: PlayerReducer
) : ViewModel() {

    val store: Store<PlayerAction, PlayerState, PlayerSideEffect>
        get() = StoreBuilder<PlayerAction, PlayerState, PlayerSideEffect>()
            .enableLogging()
            .enablePersistState("player", stateStorage)
            .reducer(playerReducer)
            .initialState(PlayerState())
            .build(scope)

}