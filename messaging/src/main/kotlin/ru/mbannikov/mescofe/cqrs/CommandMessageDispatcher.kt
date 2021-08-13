package ru.mbannikov.mescofe.cqrs

import mu.KLogging
import ru.mbannikov.mescofe.messaging.Message
import ru.mbannikov.mescofe.messaging.MessageHandlerRegistry
import ru.mbannikov.mescofe.messaging.MessageSource

class CommandMessageDispatcher(
    private val messageSource: MessageSource<*>,
    private val handlerRegistry: MessageHandlerRegistry
) {
    init {
        subscribeToMessageSource()
    }

    private fun subscribeToMessageSource() {
        println(">> Subscribe CommandMessageDispatcher to MessageSource=${messageSource::class.simpleName}")
        messageSource.subscribe { message -> dispatch(message) }
    }

    /** Передает сообщение всем обработчикам подходящим для этого сообщения. */
    private fun dispatch(message: Message<*>): Any? {
        val commandHandler = handlerRegistry.getHandlersForMessage(message).firstOrNull()
        println(">> Attempt to dispatch message=$message to a command handler=$commandHandler")
        return commandHandler?.handle(message)
    }

    companion object: KLogging()
}