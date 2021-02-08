package ru.mbannikov.mescofe.messaging

import mu.KLogging

class MessageDispatcher(
    private val messageSource: MessageSource<*>,
    private val handlerRegistry: MessageHandlerRegistry
) {
    init {
        subscribeToMessageSource()
    }

    private fun subscribeToMessageSource() {
        println(">> Subscribe MessageDispatcher to MessageSource=${messageSource::class.simpleName}")
        messageSource.subscribe { message -> dispatch(message) }
    }

    /** Передает сообщение всем обработчикам подходящим для этого сообщения. */
    private fun dispatch(message: Message<*>) {
        val messageHandlers = handlerRegistry.getHandlersForMessage(message)
        println(">> Attempt to dispatch message=$message to a message handlers=$messageHandlers") // TODO: разобраться почему не работает логгер
        messageHandlers.forEach { it.handle(message) }
    }

    companion object: KLogging()
}
