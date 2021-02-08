package ru.mbannikov.mescofe.messaging

import kotlin.reflect.KClass

abstract class MessageHandlerRegistry {

    abstract val handlers: Set<MessageHandler>

    fun getHandlersForMessage(message: Message<*>): List<MessageHandler> =
        handlers.filter { it.canHandle(message) }

    fun getHandlersForMessageType(messageType: KClass<out Message<*>>): List<MessageHandler> =
        handlers.filter { it.handleMessageType == messageType }
}
