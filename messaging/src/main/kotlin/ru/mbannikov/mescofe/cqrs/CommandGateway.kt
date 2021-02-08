package ru.mbannikov.mescofe.cqrs

import java.util.UUID

class CommandGateway(
    private val commandBus: CommandBus
) {

    /** Отправляет команду в шину. */
    fun send(command: Any): Unit = commandBus.send(commandMessage = constructCommandMessage(command))

    /** Отправляет команду в шину и блокируется на ожидание выполнения. */
    fun <T> sendAndWait(command: Any): T = commandBus.sendAndWait(commandMessage = constructCommandMessage(command))

    private fun <T : Any> constructCommandMessage(command: T) = GenericCommandMessage(
        identifier = UUID.randomUUID().toString(),
        type = command::class.simpleName!!, // TODO: сначала брать из аннотации @CommandType, а если ее нет, то имя класса
        payload = command
    )
}