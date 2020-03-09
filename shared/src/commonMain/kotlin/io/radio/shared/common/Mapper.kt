package io.radio.shared.common


@Suppress("MemberVisibilityCanBePrivate", "unused", "UNUSED_PARAMETER")
abstract class Mapper<To, From> {

    fun map(from: From): To = map(from, null)

    abstract fun map(from: From, params: Any?): To

    fun reverseMap(to: To): From = reverseMap(to, null)

    open fun reverseMap(to: To, params: Any?): From {
        throw notImplementedException()
    }

    fun notImplementedException(): NotImplementedError {
        return NotImplementedError("The method is not implemented")
    }

    fun mapList(typeList: List<From>): List<To> = mapList(typeList, null)

    open fun mapList(typeList: List<From>, params: Any? = null): List<To> = typeList.map { map(it, params) }

    fun reverseMapList(typeList: List<To>): List<From> = reverseMapList(typeList, null)

    open fun reverseMapList(typeList: List<To>, params: Any?): List<From> = typeList.map { reverseMap(it, params) }

}