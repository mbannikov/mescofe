package ru.mbannikov.mescofe.messaging

/** Абстрактный источник, откуда поступают сообщения. */
interface MessageSource<M : Message<*>> {

    fun subscribe(messageProcessor: (M) -> Any?)
}