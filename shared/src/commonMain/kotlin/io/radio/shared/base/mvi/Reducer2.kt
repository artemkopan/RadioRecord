package io.radio.shared.base.mvi

import io.radio.shared.base.Persistable
import io.radio.shared.base.mvi.middleware.Action


interface Reducer2<A : Action, S : Persistable> {

    fun reduce(action: A, state: S): S

}

