package ru.mbannikov.mescofe.eventhandling

import mu.KLogging
import ru.mbannikov.mescofe.messaging.Message
import ru.mbannikov.mescofe.messaging.MessageHandlerRegistry
import ru.mbannikov.mescofe.messaging.MessageSource

class EventMessageDispatcher(
    private val messageSource: MessageSource<*>,
    private val handlerRegistry: MessageHandlerRegistry
) {
    init {
        subscribeToMessageSource()
    }

    private fun subscribeToMessageSource() {
        println(">> Subscribe EventMessageDispatcher to MessageSource=${messageSource::class.simpleName}")
        messageSource.subscribe { message -> dispatch(message) }
    }

    /** Передает сообщение всем обработчикам подходящим для этого сообщения. */
    private fun dispatch(message: Message<*>) {
        val eventHandlers = handlerRegistry.getHandlersForMessage(message)
        println(">> Attempt to dispatch message=$message to a event handlers=$eventHandlers") // TODO: разобраться почему не работает логгер
        eventHandlers.forEach { it.handle(message) }
    }

    companion object: KLogging()
}

