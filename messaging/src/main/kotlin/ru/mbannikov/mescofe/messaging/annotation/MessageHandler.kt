package ru.mbannikov.mescofe.messaging.annotation

import ru.mbannikov.mescofe.messaging.Message
import kotlin.reflect.KClass

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MessageHandler(
    val messageType: KClass< out Message<*>>
)

