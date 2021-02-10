package ru.mbannikov.mescofe.springboot

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.core.annotation.AnnotationUtils
import ru.mbannikov.mescofe.messaging.MessageHandlerRegistry
import ru.mbannikov.mescofe.messaging.MethodMessageHandler
import ru.mbannikov.mescofe.messaging.annotation.MessageHandler
import ru.mbannikov.mescofe.utils.ReflectionUtils
import java.lang.reflect.Method

class BeanMessageHandlerRegistry(
    private val beanFactory: ConfigurableListableBeanFactory
) : MessageHandlerRegistry() {
    override val handlers: Set<ru.mbannikov.mescofe.messaging.MessageHandler> = findHandlers()

    private fun findHandlers(): Set<ru.mbannikov.mescofe.messaging.MessageHandler> {
        val result = mutableSetOf<ru.mbannikov.mescofe.messaging.MessageHandler>()

        beanFactory.beanNamesIterator.forEach { beanName ->
            val beanType: Class<*> = beanFactory.getType(beanName) ?: return@forEach
            val containsBeanDefinition: Boolean = beanFactory.containsBeanDefinition(beanName)
            val isSingleton: Boolean = containsBeanDefinition && beanFactory.getBeanDefinition(beanName).isSingleton

            if (containsBeanDefinition && isSingleton) {
                val classMethods: Iterable<Method> = ReflectionUtils.methodsOf(clazz = beanType)
                classMethods.forEach { method ->
                    val messageHandler: MessageHandler? = AnnotationUtils.findAnnotation(method, MessageHandler::class.java)
                    // TODO: и чего дальше делать с messageHandler::messageType

                    if (messageHandler != null) {
                        val bean: Any = beanFactory.getBean(beanName)
                        val handler = MethodMessageHandler(target = bean, method = method.also { it.trySetAccessible() })

                        result.add(handler)
                    }
                }
            }
        }

        return result
    }
}