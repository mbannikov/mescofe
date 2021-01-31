package ru.mbannikov.mescofe.eventhandling

abstract class AbstractEventBus : EventBus {
    private val messageProcessors: MutableList<MessageProcessor> = mutableListOf()

    override fun subscribe(messageProcessor: MessageProcessor) {
        messageProcessors.add(messageProcessor)
    }

    protected fun processMessage(eventMessage: EventMessage<*>) {
        messageProcessors.forEach { processor ->
            processor(eventMessage)
        }
    }
}

private typealias MessageProcessor = (EventMessage<*>) -> Unit
