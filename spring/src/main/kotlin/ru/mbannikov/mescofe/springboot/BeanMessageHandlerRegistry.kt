package ru.mbannikov.mescofe.springboot

import org.springframework.core.annotation.AnnotationUtils
import ru.mbannikov.mescofe.messaging.MessageHandlerRegistry
import ru.mbannikov.mescofe.messaging.MethodMessageHandler
import ru.mbannikov.mescofe.messaging.annotation.MessageHandler
import ru.mbannikov.mescofe.utils.ReflectionUtils
import java.lang.reflect.Method

class BeanMessageHandlerRegistry(
    messageHandlerBeans: Collection<Any>
) : MessageHandlerRegistry() {

    override val handlers: Set<ru.mbannikov.mescofe.messaging.MessageHandler> = buildMethodMessageHandlers(messageHandlerBeans)

    private fun buildMethodMessageHandlers(messageHandlerBeans: Collection<Any>) =
        messageHandlerBeans.map { bean ->
            val classMethods: Iterable<Method> = ReflectionUtils.methodsOf(clazz = bean::class.java)
            classMethods.mapNotNull { method ->
                val messageHandler = AnnotationUtils.findAnnotation(method, MessageHandler::class.java)
                if (messageHandler != null)
                    MethodMessageHandler(target = bean, method = method, messageType = messageHandler.messageType)
                else
                    null
            }
        }.flatten().toSet()
}
