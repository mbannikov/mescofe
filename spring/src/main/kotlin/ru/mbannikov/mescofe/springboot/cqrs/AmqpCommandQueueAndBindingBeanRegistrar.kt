package ru.mbannikov.mescofe.springboot.cqrs

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Exchange
import org.springframework.amqp.core.Queue
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import ru.mbannikov.mescofe.cqrs.CommandMessage
import ru.mbannikov.mescofe.messaging.MessageHandlerRegistry

class AmqpCommandQueueAndBindingBeanRegistrar(
    private val exchange: Exchange,
    private val queueRegistry: CommandBusQueueRegistry
) : BeanFactoryAware {

    override fun setBeanFactory(beanFactory: BeanFactory) {
        registerQueuesAndBindings(beanFactory as ConfigurableListableBeanFactory)
    }

    private fun registerQueuesAndBindings(beanFactory: ConfigurableListableBeanFactory) {
        val messageHandlerRegistry: MessageHandlerRegistry = beanFactory.getBean(MessageHandlerRegistry::class.java)
        val commandTypes: Collection<String> = messageHandlerRegistry
            .getHandlersForMessageType(CommandMessage::class)
            .map { it.handlePayloadType.simpleName!! }

        commandTypes.forEach { commandType ->
            val queue = createAndRegisterQueue(commandType, beanFactory)
            createAndRegisterBinding(commandType, queue, beanFactory)

            queueRegistry.register(queue)
        }
    }

    private fun createAndRegisterQueue(commandName: String, beanFactory: ConfigurableListableBeanFactory): Queue {
        val beanName = "${commandName}Queue"
        val beanInstance = Queue(commandName)

        beanFactory.registerSingleton(beanName, beanInstance)

        return beanInstance
    }

    private fun createAndRegisterBinding(commandName: String, queue: Queue, beanFactory: ConfigurableListableBeanFactory) {
        val beanName = "${commandName}Binding"
        val beanInstance: Binding = BindingBuilder.bind(queue).to(exchange).with(commandName).noargs()

        beanFactory.registerSingleton(beanName, beanInstance)
    }
}

