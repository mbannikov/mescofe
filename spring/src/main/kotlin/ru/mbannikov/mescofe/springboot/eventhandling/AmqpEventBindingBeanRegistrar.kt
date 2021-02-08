package ru.mbannikov.mescofe.springboot.eventhandling

import mu.KLogging
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Exchange
import org.springframework.amqp.core.Queue
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import ru.mbannikov.mescofe.eventhandling.EventMessage
import ru.mbannikov.mescofe.messaging.MessageHandlerRegistry

class AmqpEventBindingBeanRegistrar(
    private val exchange: Exchange,
    private val queue: Queue,
) : BeanFactoryAware {

    override fun setBeanFactory(beanFactory: BeanFactory) {
        registerBindings(beanFactory as ConfigurableListableBeanFactory)
    }

    private fun registerBindings(beanFactory: ConfigurableListableBeanFactory) {
        val messageHandlerRegistry: MessageHandlerRegistry = beanFactory.getBean(MessageHandlerRegistry::class.java)
        val eventTypes: Collection<String> = messageHandlerRegistry
            .getHandlersForMessageType(EventMessage::class)
            .map { it.handlePayloadType.simpleName!! }

        eventTypes.forEach { eventType ->
            val beanName = "${eventType}QueueBinding"
            val beanInstance: Binding = createBinding(eventType)

            logger.debug { "Register amqp binding bean=$beanName for event=$eventType" }

            beanFactory.registerSingleton(beanName, beanInstance)
        }
    }

    private fun createBinding(eventName: String): Binding =
        BindingBuilder.bind(queue).to(exchange).with(eventName).noargs()

    companion object : KLogging()
}
