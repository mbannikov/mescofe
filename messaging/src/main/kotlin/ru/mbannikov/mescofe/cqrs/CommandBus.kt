package ru.mbannikov.mescofe.cqrs

import ru.mbannikov.mescofe.messaging.MessageSource

interface CommandBus : MessageSource<CommandMessage<*>> {

    /** Отправляет команду в шину. */
    fun send(commandMessage: CommandMessage<*>)

    /** Отправляет команду в шину и блокируется на ожидание выполнения. */
    fun <T> sendAndWait(commandMessage: CommandMessage<*>): T
}