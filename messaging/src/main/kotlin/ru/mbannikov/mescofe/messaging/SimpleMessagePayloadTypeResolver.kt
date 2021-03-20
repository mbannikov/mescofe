package ru.mbannikov.mescofe.messaging

import kotlin.reflect.KClass

class SimpleMessagePayloadTypeResolver : MessagePayloadTypeResolver {
    private val payloadTypeMap = mutableMapOf<String, KClass<*>>()

    override fun resolvePayloadType(payloadType: String): KClass<*>? =
        payloadTypeMap[payloadType]

    fun registerPayloadType(payloadType: String, clazz: KClass<*>) {
        payloadTypeMap[payloadType] = clazz
    }
}