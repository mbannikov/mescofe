package ru.mbannikov.mescofe.springboot

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.config.RuntimeBeanReference
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.beans.factory.support.ManagedList
import org.springframework.core.annotation.AnnotationUtils
import ru.mbannikov.mescofe.messaging.annotation.MessageHandler
import ru.mbannikov.mescofe.utils.ReflectionUtils
import java.lang.reflect.Method

class MessageHandlerRegistryBeanDefinitionRegistryPostProcessor : BeanDefinitionRegistryPostProcessor {
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {}

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        val messageHandlers = ManagedList<Any>().apply {
            addAll(findHandlers(registry))
        }

        val messageHandlerRegistryBeanDefinition: BeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(BeanMessageHandlerRegistry::class.java)
            .addConstructorArgValue(messageHandlers)
            .beanDefinition

        registry.registerBeanDefinition("messageHandlerRegistry", messageHandlerRegistryBeanDefinition)
    }

    private fun findHandlers(registry: BeanDefinitionRegistry): Set<Any> {
        val result = mutableSetOf<Any>()

        registry.beanDefinitionNames.forEach { beanName ->
            val beanDefinition = registry.getBeanDefinition(beanName)
            val beanType: Class<*>? = beanDefinition.beanClassName?.let { Class.forName(it) }
            val isSingleton: Boolean = beanDefinition.isSingleton

            if (beanType != null && isSingleton) {
                val classMethods: Iterable<Method> = ReflectionUtils.methodsOf(clazz = beanType)
                classMethods.forEach { method ->
                    val hasHandler: Boolean = AnnotationUtils.findAnnotation(method, MessageHandler::class.java) != null

                    if (hasHandler) {
                        result.add(RuntimeBeanReference(beanName))
                    }
                }
            }
        }

        return result
    }
}