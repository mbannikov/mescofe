package ru.mbannikov.mescofe.cqrs

abstract class AbstractCommandBus : CommandBus {
    private var messageProcessor: MessageProcessor? = null

    override fun subscribe(messageProcessor: MessageProcessor) {
        require(this.messageProcessor == null) { "The CommandBus can have only one subscriber" }

        this.messageProcessor = messageProcessor
    }

    protected fun processMessage(commandMessage: CommandMessage<*>): Any? = messageProcessor?.invoke(commandMessage)
}

private typealias MessageProcessor = (CommandMessage<*>) -> Any?
