package ru.mbannikov.mescofe.messaging

import kotlin.reflect.KClass

interface MessagePayloadTypeResolver {
    fun resolvePayloadType(payloadType: String): KClass<*>?
}

