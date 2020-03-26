package io.radio.shared.base


interface UseCase<Params, Return> {

    suspend fun execute(params: Params): Return

}
