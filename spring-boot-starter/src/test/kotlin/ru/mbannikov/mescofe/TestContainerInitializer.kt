package ru.mbannikov.mescofe

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource
import org.testcontainers.containers.RabbitMQContainer

class TestContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val rabbitMQContainer = RabbitMQContainer("rabbitmq:3-management").also { it.start() }

        val props = mapOf(
            "spring.rabbitmq.host" to rabbitMQContainer.host,
            "spring.rabbitmq.port" to rabbitMQContainer.amqpPort
        )
        val propertySource = MapPropertySource("testcontainers", props)
        applicationContext.environment.propertySources.addFirst(propertySource)
    }
}