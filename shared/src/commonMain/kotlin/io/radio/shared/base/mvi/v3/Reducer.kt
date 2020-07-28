package io.radio.shared.base.mvi.v3


typealias Reducer<A, S> = S.(A) -> S

