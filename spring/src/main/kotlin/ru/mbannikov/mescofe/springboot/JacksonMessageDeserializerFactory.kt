package ru.mbannikov.mescofe.springboot

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import ru.mbannikov.mescofe.cqrs.CommandMessage
import ru.mbannikov.mescofe.cqrs.CommandResultMessage
import ru.mbannikov.mescofe.cqrs.GenericCommandMessage
import ru.mbannikov.mescofe.cqrs.GenericCommandResultMessage
import ru.mbannikov.mescofe.eventhandling.EventMessage
import ru.mbannikov.mescofe.eventhandling.GenericEventMessage
import ru.mbannikov.mescofe.messaging.Message
import kotlin.reflect.KClass

class JacksonMessageDeserializerFactory(
    private val eventMessageDeserializer: JacksonMessageDeserializer,
    private val commandMessageDeserializer: JacksonMessageDeserializer,
    private val commandResultMessageDeserializer: JacksonMessageDeserializer
) {
    fun <T : Message<*>> getDeserializer(clazz: KClass<T>): JsonDeserializer<T> =
        when (clazz) {
            EventMessage::class -> constructEventMessageDeserializer() as JsonDeserializer<T>
            CommandMessage::class -> constructCommandMessageDeserializer() as JsonDeserializer<T>
            CommandResultMessage::class -> constructCommandResultMessageDeserializer() as JsonDeserializer<T>
            else -> throw IllegalAccessException("Can't construct JsonDeserializer for $clazz")
        }

    private fun constructEventMessageDeserializer(): JsonDeserializer<EventMessage<*>> =
        object : StdDeserializer<EventMessage<*>>(EventMessage::class.java) {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): EventMessage<*> {
                val messageContainer = eventMessageDeserializer.deserialize(parser)
                return GenericEventMessage(
                    identifier = messageContainer.identifier,
                    type = messageContainer.type,
                    payload = messageContainer.payload
                )
            }
        }

    private fun constructCommandMessageDeserializer(): JsonDeserializer<CommandMessage<*>> =
        object : StdDeserializer<CommandMessage<*>>(CommandMessage::class.java) {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): CommandMessage<*> {
                val messageContainer = commandMessageDeserializer.deserialize(parser)
                return GenericCommandMessage(
                    identifier = messageContainer.identifier,
                    type = messageContainer.type,
                    payload = messageContainer.payload
                )
            }
        }

    private fun constructCommandResultMessageDeserializer(): JsonDeserializer<CommandResultMessage<*>> =
        object : StdDeserializer<CommandResultMessage<*>>(CommandResultMessage::class.java) {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): CommandResultMessage<*> {
                val messageContainer = commandResultMessageDeserializer.deserialize(parser)
                return GenericCommandResultMessage(
                    identifier = messageContainer.identifier,
                    type = messageContainer.type,
                    payload = messageContainer.payload
                )
            }
        }
}