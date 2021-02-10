package ru.mbannikov.mescofe.messaging

import java.lang.reflect.Method
import kotlin.reflect.KClass

class MethodMessageHandler(
    private val target: Any,
    private val method: Method
) : MessageHandler {

    override val handlePayloadType: KClass<*> = run {
        val parameters = method.parameters
        val handlerHasZeroArguments = parameters.isEmpty()
        val handlerHasMoreThanOneParameter = parameters.size > 1

        when {
            handlerHasZeroArguments -> throw IllegalArgumentException("Message handler doesn't have specified message type")
            handlerHasMoreThanOneParameter -> throw IllegalArgumentException("Message handler has more than one arguments")
            else -> parameters.first().type.kotlin
        }
    }

    override fun canHandle(message: Message<*>): Boolean =
        canHandleType(message.payload::class)

    override fun canHandleType(payloadType: KClass<*>): Boolean =
        this.handlePayloadType.java.isAssignableFrom(payloadType.java)

    override fun handle(message: Message<*>): Any? {
        method.trySetAccessible()
        return method.invoke(target, message.payload)
    }
}