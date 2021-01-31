package ru.mbannikov.mescofe.springboot

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.module.kotlin.treeToValue
import ru.mbannikov.mescofe.eventhandling.EventMessage
import ru.mbannikov.mescofe.eventhandling.GenericEventMessage
import ru.mbannikov.mescofe.messaging.MessageHandlerRegistry
import kotlin.reflect.KClass

class AmqpEventMessageDeserializer(
    private val messageHandlerRegistry: MessageHandlerRegistry
) : StdDeserializer<GenericEventMessage<*>>(GenericEventMessage::class.java) {
    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): GenericEventMessage<*> {
        val objectMapper = parser.codec as ObjectMapper
        val node: JsonNode = parser.codec.readTree(parser)

        val payloadType: String = (node.get(EventMessage<*>::type.name) as TextNode).textValue()
        val payloadClass: Class<*> = getPayloadClassByType(payloadType)
        val payloadNode: JsonNode = node.get(EventMessage<*>::payload.name)
        val payload = objectMapper.treeToValue(payloadNode, payloadClass)

        val wrapper: Wrapper = objectMapper.treeToValue(node)!!
        return GenericEventMessage(identifier = wrapper.identifier, type = wrapper.type, payload = payload)
    }

    private fun getPayloadClassByType(payloadType: String): Class<*> {
        val suitableClassesForPayload: List<KClass<*>> = messageHandlerRegistry.handlers
            .map { it.handlePayloadType }
            .filter { clazz -> clazz.simpleName == payloadType }

        if (suitableClassesForPayload.isEmpty())
            throw Exception() // TODO
        if (suitableClassesForPayload.size > 1)
            throw Exception() // TODO

        return suitableClassesForPayload.first().java
    }

    private data class Wrapper(
        override val identifier: String,
        override val type: String,
        override val payload: Any
    ) : EventMessage<Any>
}