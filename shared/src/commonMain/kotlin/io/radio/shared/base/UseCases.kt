package io.radio.shared.base


interface UseCase<Params, Return> {

    suspend fun execute(params: Params): Return

}

interface UseCasesBiParams<Params1, Params2, Return>{

    suspend fun execute(params1: Params1, params2: Params2): Return

}