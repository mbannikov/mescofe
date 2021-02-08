package ru.mbannikov.mescofe.springboot

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Exchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import ru.mbannikov.mescofe.cqrs.CommandBus
import ru.mbannikov.mescofe.cqrs.CommandGateway
import ru.mbannikov.mescofe.messaging.MessageDispatcher
import ru.mbannikov.mescofe.messaging.MessageHandlerRegistry
import ru.mbannikov.mescofe.springboot.cqrs.AmqpCommandBus
import ru.mbannikov.mescofe.springboot.cqrs.AmqpCommandQueueAndBindingBeanRegistrar
import ru.mbannikov.mescofe.springboot.cqrs.CommandBusQueueRegistry
import ru.mbannikov.mescofe.springboot.eventhandling.AmqpEventBindingBeanRegistrar

@Configuration
class AmqpAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun objectMapper(): ObjectMapper =
        jacksonObjectMapper()

    @Bean
    @ConditionalOnMissingBean
    fun jackson2JsonMessageConverter(objectMapper: ObjectMapper): Jackson2JsonMessageConverter =
        Jackson2JsonMessageConverter(objectMapper)

    @Bean
    @ConditionalOnMissingBean
    fun mappingJackson2MessageConverter(objectMapper: ObjectMapper): MappingJackson2MessageConverter =
        MappingJackson2MessageConverter().apply {
            this.objectMapper = objectMapper
        }

    @Bean
    fun amqpConfigurationBeanPostProcessor(messageHandlerRegistry: MessageHandlerRegistry): AmqpConfigurationBeanPostProcessor {
        val deserializer = JacksonMessageDeserializer(messageHandlerRegistry)
        val deserializerFactory = JacksonMessageDeserializerFactory(deserializer)

        return AmqpConfigurationBeanPostProcessor(deserializerFactory)
    }

    @Bean
    fun eventBusExchange(): Exchange =
        DirectExchange(EVENT_BUS_EXCHANGE_NAME)

    @Bean
    fun eventQueue(@Value("\${spring.application.name}") applicationName: String): Queue {
        val queueName = "${applicationName}.events"
        return Queue(queueName)
    }

    @Bean
    fun eventsBindingRegistrar(
        @Qualifier("eventBusExchange") exchange: Exchange,
        @Qualifier("eventQueue") queue: Queue
    ): AmqpEventBindingBeanRegistrar = AmqpEventBindingBeanRegistrar(exchange, queue)





    @Bean
    fun commandBusExchange(): Exchange =
        DirectExchange(COMMAND_BUS_EXCHANGE_NAME)

    @Bean
    @ConditionalOnMissingBean
    @DependsOn("commandQueueAndBindingRegistrar")
    fun commandBus(
        @Qualifier("commandBusExchange") exchange: Exchange,
        rabbitTemplate: RabbitTemplate
    ): CommandBus = AmqpCommandBus(exchange, rabbitTemplate)

    @Bean
    @ConditionalOnMissingBean
    fun commandGateway(commandBus: CommandBus): CommandGateway =
        CommandGateway(commandBus)

    @Bean
    fun commandDispatcher(commandBus: CommandBus, handlerRegistry: MessageHandlerRegistry) =
        MessageDispatcher(commandBus, handlerRegistry)

    @Bean
    fun commandQueueAndBindingRegistrar(
        @Qualifier("commandBusExchange") exchange: Exchange,
        queueRegistry: CommandBusQueueRegistry
    ): AmqpCommandQueueAndBindingBeanRegistrar = AmqpCommandQueueAndBindingBeanRegistrar(exchange, queueRegistry)

    @Bean
    fun commandBusQueueRegistry(): CommandBusQueueRegistry =
        CommandBusQueueRegistry()

    companion object {
        const val EVENT_BUS_EXCHANGE_NAME = "event-bus"
        const val COMMAND_BUS_EXCHANGE_NAME = "command-bus"
    }
}

