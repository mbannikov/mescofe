package ru.mbannikov.mescofe.messaging

abstract class MessageHandlerRegistry {

    abstract val handlers: Set<MessageHandler>

    fun getHandlersForMessage(message: Message<*>): List<MessageHandler> =
        handlers.filter { it.canHandle(message) }
}
