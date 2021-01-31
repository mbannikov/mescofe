package ru.mbannikov.mescofe.springboot

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Exchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import ru.mbannikov.mescofe.eventhandling.GenericEventMessage
import ru.mbannikov.mescofe.messaging.MessageHandlerRegistry

@Configuration
class AmqpAutoConfiguration {

    @Bean
    fun amqpObjectMapper(messageHandlerRegistry: MessageHandlerRegistry): ObjectMapper =
        jacksonObjectMapper()
            .registerModule(
                SimpleModule().addDeserializer(GenericEventMessage::class.java, AmqpEventMessageDeserializer(messageHandlerRegistry))
            )
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    @Bean
    fun jackson2Json(@Qualifier("amqpObjectMapper") objectMapper: ObjectMapper): Jackson2JsonMessageConverter =
        Jackson2JsonMessageConverter(objectMapper)

    @Bean
    fun mappingJackson(@Qualifier("amqpObjectMapper") objectMapper: ObjectMapper): MappingJackson2MessageConverter {
        val converter = MappingJackson2MessageConverter()
        converter.objectMapper = objectMapper

        return converter
    }

    @Bean
    fun eventBusExchange(): Exchange = DirectExchange(EVENT_BUS_EXCHANGE_NAME)

    @Bean
    fun eventQueue(@Value("\${spring.application.name}") applicationName: String) = Queue("${applicationName}.events")

    @Bean
    fun bindingRegistrar(
        @Qualifier("eventBusExchange") exchange: Exchange,
        @Qualifier("eventQueue") queue: Queue,
        beanFactory: ConfigurableBeanFactory,
        messageHandlerRegistry: MessageHandlerRegistry
    ): AmqpBindingBeanRegistrar {
        val eventTypes: Collection<String> = messageHandlerRegistry.handlers.map { it.handlePayloadType.simpleName!! }
        return AmqpBindingBeanRegistrar(exchange, queue, eventTypes, beanFactory)
    }

    companion object {
        const val EVENT_BUS_EXCHANGE_NAME = "event-bus"
    }
}
