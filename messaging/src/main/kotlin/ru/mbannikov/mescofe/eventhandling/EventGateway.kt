package ru.mbannikov.mescofe.eventhandling

import java.util.UUID

/** User friendly аналог EventBus для отправки событий в шину. */
class EventGateway(
    private val eventBus: EventBus
) {
    /** Публикует событие в шину. */
    fun publish(event: Any) {
        val eventMessage = GenericEventMessage(
            identifier = UUID.randomUUID().toString(),
            type = event::class.simpleName!!, // TODO: сначала брать из аннотации @EventType, а если ее нет, то имя класса
            payload = event
        )

        eventBus.publish(eventMessage)
    }

    /** Публикует несколько событий в шину. */
    fun publish(vararg events: Any) {
        events.forEach(::publish)
    }
}