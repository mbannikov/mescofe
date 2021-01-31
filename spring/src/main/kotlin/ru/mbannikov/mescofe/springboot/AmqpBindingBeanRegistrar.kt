package ru.mbannikov.mescofe.springboot

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Exchange
import org.springframework.amqp.core.Queue
import org.springframework.beans.factory.config.ConfigurableBeanFactory

class AmqpBindingBeanRegistrar(
    private val exchange: Exchange,
    private val queue: Queue,
    private val eventTypes: Collection<String>,
    private val beanFactory: ConfigurableBeanFactory
) {
    init {
        registerBindings()
    }

    private fun registerBindings() {
        eventTypes.asSequence()
            .map { eventType -> eventType to createBinding(eventType) }
            .forEach { (eventType, binding) -> registerBindingAsBean(beanName = "${eventType}QueueBinding", binding = binding)}
    }

    private fun createBinding(eventName: String): Binding =
        BindingBuilder.bind(queue).to(exchange).with(eventName).noargs()

    private fun registerBindingAsBean(beanName: String, binding: Binding) {
        beanFactory.registerSingleton(beanName, binding)
    }
}