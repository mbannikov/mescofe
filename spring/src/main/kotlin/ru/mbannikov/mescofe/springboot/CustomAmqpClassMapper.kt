package ru.mbannikov.mescofe.springboot

import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.support.converter.ClassMapper
import ru.mbannikov.mescofe.cqrs.CommandMessage
import ru.mbannikov.mescofe.eventhandling.EventMessage
import java.util.LinkedHashMap

open class CustomAmqpClassMapper : ClassMapper {

    override fun fromClass(clazz: Class<*>, properties: MessageProperties) {
        val messageType: String = when {
            EventMessage::class.java.isAssignableFrom(clazz) -> EventMessage::class.java
            CommandMessage::class.java.isAssignableFrom(clazz) -> CommandMessage::class.java
            else -> clazz
        }.simpleName

        properties.headers[MESSAGE_TYPE_FIELD_NAME] = messageType
    }

    override fun toClass(properties: MessageProperties): Class<*> {
        val messageType: String? = properties.headers[MESSAGE_TYPE_FIELD_NAME] as? String

        return when (messageType) {
            EventMessage::class.java.simpleName -> EventMessage::class.java
            CommandMessage::class.java.simpleName -> CommandMessage::class.java
            else -> DEFAULT_CLASS
        }
    }

    companion object {
        private const val MESSAGE_TYPE_FIELD_NAME = "MessageType"
        private val DEFAULT_CLASS = LinkedHashMap::class.java
    }
}