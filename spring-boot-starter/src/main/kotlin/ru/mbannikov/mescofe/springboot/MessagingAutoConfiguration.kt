package ru.mbannikov.mescofe.springboot

import org.springframework.amqp.core.Exchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.mbannikov.mescofe.eventhandling.EventBus
import ru.mbannikov.mescofe.eventhandling.EventGateway
import ru.mbannikov.mescofe.messaging.MessageDispatcher
import ru.mbannikov.mescofe.messaging.MessageHandlerRegistry

@Configuration
class MessagingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun eventBus(
        @Qualifier("eventBusExchange") exchange: Exchange,
        rabbitTemplate: RabbitTemplate
    ): EventBus = AmqpEventBus(exchange, rabbitTemplate)

    @Bean
    @ConditionalOnMissingBean
    fun eventGateway(eventBus: EventBus): EventGateway = EventGateway(eventBus)

    @Bean
    @ConditionalOnMissingBean
    fun messageHandlerRegistry(beanFactory: ConfigurableListableBeanFactory): MessageHandlerRegistry = BeanMessageHandlerRegistry(beanFactory)

    @Bean
    @ConditionalOnMissingBean
    fun eventDispatcher(eventBus: EventBus, handlerRegistry: MessageHandlerRegistry) = MessageDispatcher(eventBus, handlerRegistry)
}

