package ru.mbannikov.mescofe.springboot

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.module.kotlin.treeToValue
import ru.mbannikov.mescofe.messaging.Message
import ru.mbannikov.mescofe.messaging.MessagePayloadTypeResolver

class JacksonMessageDeserializer(
    private val payloadTypeResolver: MessagePayloadTypeResolver
) {
    fun deserialize(parser: JsonParser): MessageDeserializerContainer {
        val objectMapper = parser.codec as ObjectMapper
        val node: JsonNode = parser.codec.readTree(parser)

        val payloadType: String = (node.get(Message<*>::type.name) as TextNode).textValue()
        val payloadClass: Class<*> = getPayloadClassByType(payloadType)
        val payloadNode: JsonNode = node.get(Message<*>::payload.name)
        val payload = objectMapper.treeToValue(payloadNode, payloadClass)

        val messageContainer: MessageDeserializerContainer = objectMapper.treeToValue(node)!!
        return messageContainer.copy(payload = payload)
    }

    private fun getPayloadClassByType(payloadType: String): Class<*> {
        val payloadClass = payloadTypeResolver.resolvePayloadType(payloadType)?.java
        return payloadClass ?: throw Exception() // TODO: исправить исключение на подходящее
    }

    data class MessageDeserializerContainer(
        override val identifier: String,
        override val type: String,
        override val payload: Any
    ) : Message<Any>
}
