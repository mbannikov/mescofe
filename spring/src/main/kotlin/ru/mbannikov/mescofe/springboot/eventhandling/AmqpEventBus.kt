package ru.mbannikov.mescofe.springboot.eventhandling

import mu.KLogging
import org.springframework.amqp.core.Exchange
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import ru.mbannikov.mescofe.eventhandling.AbstractEventBus
import ru.mbannikov.mescofe.eventhandling.EventMessage

@RabbitListener(queues = ["#{eventQueue.name}"])
class AmqpEventBus(
    private val exchange: Exchange,
    private val rabbitTemplate: RabbitTemplate
) : AbstractEventBus() {

    override fun publish(eventMessage: EventMessage<*>) {
        logger.info { "Publish an event message=${eventMessage.type} to an event bus" }
        logger.debug { "message=$eventMessage" }

        val routingKey = eventMessage.type
        rabbitTemplate.convertAndSend(exchange.name, routingKey, eventMessage)
    }

    @RabbitHandler
    fun handleMessage(eventMessage: EventMessage<*>) {
        logger.info { "The event bus received an event message=${eventMessage.type}" }
        logger.debug { "message=$eventMessage" }

        processMessage(eventMessage)
    }

    companion object: KLogging()
}
