package ru.mbannikov.mescofe.cqrs

abstract class AbstractCommandBus : CommandBus {
    private val messageProcessors: MutableList<MessageProcessor> = mutableListOf()

    override fun subscribe(messageProcessor: MessageProcessor) {
        messageProcessors.add(messageProcessor)
    }

    protected fun processMessage(eventMessage: CommandMessage<*>) {
        messageProcessors.forEach { processor ->
            processor(eventMessage)
        }
    }
}

private typealias MessageProcessor = (CommandMessage<*>) -> Unit
