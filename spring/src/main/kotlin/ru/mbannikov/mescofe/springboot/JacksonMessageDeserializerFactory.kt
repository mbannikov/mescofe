package ru.mbannikov.mescofe.springboot

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import ru.mbannikov.mescofe.cqrs.CommandMessage
import ru.mbannikov.mescofe.cqrs.GenericCommandMessage
import ru.mbannikov.mescofe.eventhandling.EventMessage
import ru.mbannikov.mescofe.eventhandling.GenericEventMessage
import ru.mbannikov.mescofe.messaging.Message
import kotlin.reflect.KClass

class JacksonMessageDeserializerFactory(
    private val messageDeserializer: JacksonMessageDeserializer
) {
    fun <T : Message<*>> getDeserializer(clazz: KClass<T>): JsonDeserializer<T> =
        when (clazz) {
            EventMessage::class -> constructEventMessageDeserializer() as JsonDeserializer<T>
            CommandMessage::class -> constructCommandMessageDeserializer() as JsonDeserializer<T>
            else -> throw IllegalAccessException("Can't construct JsonDeserializer for $clazz")
        }

    private fun constructEventMessageDeserializer(): JsonDeserializer<EventMessage<*>> =
        object : StdDeserializer<EventMessage<*>>(EventMessage::class.java) {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): EventMessage<*> {
                val messageContainer = messageDeserializer.deserialize(parser)
                return GenericEventMessage(identifier = messageContainer.identifier, type = messageContainer.type, payload = messageContainer.payload)
            }
        }

    private fun constructCommandMessageDeserializer(): JsonDeserializer<CommandMessage<*>> =
        object : StdDeserializer<CommandMessage<*>>(CommandMessage::class.java) {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): CommandMessage<*> {
                val messageContainer = messageDeserializer.deserialize(parser)
                return GenericCommandMessage(
                    identifier = messageContainer.identifier,
                    type = messageContainer.type,
                    payload = messageContainer.payload
                )
            }
        }
}