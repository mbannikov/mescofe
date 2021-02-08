package ru.mbannikov.mescofe.springboot.cqrs

import mu.KLogging
import org.springframework.amqp.core.Exchange
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import ru.mbannikov.mescofe.cqrs.AbstractCommandBus
import ru.mbannikov.mescofe.cqrs.CommandMessage

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
        return rabbitTemplate.convertSendAndReceive(exchange.name, routingKey, commandMessage) as? T
            ?: throw Exception("Can't cast command response") // TODO: сделать нормальное исключение
    }

    // TODO: возвращать CommandResultMessage для работы sendAndWait
    @RabbitHandler
    fun handleMessage(commandMessage: CommandMessage<*>) {
        logger.info { "The command bus received a command message=${commandMessage.type}" }
        logger.debug { "message=$commandMessage" }

        processMessage(commandMessage)
    }

    companion object: KLogging()
}
