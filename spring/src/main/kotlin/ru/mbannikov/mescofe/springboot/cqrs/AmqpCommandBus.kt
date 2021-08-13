package ru.mbannikov.mescofe.springboot.cqrs

import mu.KLogging
import org.springframework.amqp.core.Exchange
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import ru.mbannikov.mescofe.cqrs.AbstractCommandBus
import ru.mbannikov.mescofe.cqrs.CommandMessage
import ru.mbannikov.mescofe.cqrs.CommandResultMessage
import ru.mbannikov.mescofe.cqrs.GenericCommandResultMessage
import java.util.UUID

@RabbitListener(queues = ["#{commandBusQueueRegistry.queueNames}"])
class AmqpCommandBus(
    private val exchange: Exchange,
    private val rabbitTemplate: RabbitTemplate,
) : AbstractCommandBus() {

    override fun send(commandMessage: CommandMessage<*>) {
        logger.info { "Sending a command message=${commandMessage.type} to a command bus" }
        logger.debug { "message=$commandMessage" }

        val routingKey = commandMessage.type
        rabbitTemplate.convertAndSend(exchange.name, routingKey, commandMessage)
    }

    override fun <T> sendAndWait(commandMessage: CommandMessage<*>): T {
        logger.info { "Sending a command message=${commandMessage.type} to a command bus" }
        logger.debug { "message=$commandMessage" }

        val routingKey = commandMessage.type
        val resultMessage: CommandResultMessage<*> = rabbitTemplate.convertSendAndReceive(exchange.name, routingKey, commandMessage) as CommandResultMessage<*>

        return resultMessage.payload as? T
            ?: throw Exception("Can't cast command result") // TODO: сделать нормальное исключение
    }

    @RabbitHandler
    fun handleMessage(commandMessage: CommandMessage<*>): CommandResultMessage<*>? {
        logger.info { "The command bus received a command message=${commandMessage.type}" }
        logger.debug { "message=$commandMessage" }

        return processMessage(commandMessage)?.let { commandResult ->
            GenericCommandResultMessage(
                identifier = UUID.randomUUID().toString(),
                type = commandResult::class.simpleName!!, // TODO: сначала брать из аннотации @CommandResultType, а если ее нет, то имя класса
                payload = commandResult
            )
        }
    }

    companion object: KLogging()
}
