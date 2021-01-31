package ru.mbannikov.mescofe.messaging

class MessageDispatcher(
    messageSource: MessageSource<*>,
    private val handlerRegistry: MessageHandlerRegistry
) {
    init {
        messageSource.subscribe { message -> dispatch(message) }
    }

    /** Передает сообщение всем обработчикам подходящим для этого сообщения. */
    private fun dispatch(message: Message<*>) {
        handlerRegistry.getHandlersForMessage(message)
            .forEach { it.handle(message) }
    }
}
