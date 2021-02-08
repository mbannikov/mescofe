package ru.mbannikov.mescofe.springboot

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import ru.mbannikov.mescofe.cqrs.CommandMessage
import ru.mbannikov.mescofe.eventhandling.EventMessage

class AmqpConfigurationBeanPostProcessor(
    private val deserializerFactory: JacksonMessageDeserializerFactory
) : BeanPostProcessor, BeanFactoryAware {

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        when (bean) {
            is MappingJackson2MessageConverter -> postProcessMappingJackson2MessageConverter(bean)
        }

        return bean
    }

    private fun postProcessMappingJackson2MessageConverter(messageConverter: MappingJackson2MessageConverter) {
        val objectMapper: ObjectMapper = messageConverter.objectMapper
        val simpleModule: SimpleModule = SimpleModule()
            .addDeserializer(EventMessage::class.java, deserializerFactory.getDeserializer(EventMessage::class))
            .addDeserializer(CommandMessage::class.java, deserializerFactory.getDeserializer(CommandMessage::class))

        objectMapper
            .registerModule(simpleModule)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    private fun postProcessJackson2JsonMessageConverter(messageConverter: Jackson2JsonMessageConverter) {
        messageConverter.classMapper = CustomAmqpClassMapper()
    }

    override fun setBeanFactory(beanFactory: BeanFactory) {
        // BeanPostProcessor не срабатывает для Jackson2JsonMessageConverter, так как создается на этапе раньше в RabbitTemplateConfiguration
        // для обхода используется BeanFactoryAware
        val messageConverter = beanFactory.getBean(Jackson2JsonMessageConverter::class.java)
        postProcessJackson2JsonMessageConverter(messageConverter)
    }
}