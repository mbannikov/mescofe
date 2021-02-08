package ru.mbannikov.mescofe.messaging

import kotlin.reflect.KClass

// TODO: подумать над неймингом, так как уже есть такая аннотация
interface MessageHandler {
    val handleMessageType: KClass<out Message<*>>

    val handlePayloadType: KClass<*>

    fun canHandle(message: Message<*>): Boolean

    fun canHandleType(payloadType: KClass<*>): Boolean

    fun handle(message: Message<*>): Any?
}