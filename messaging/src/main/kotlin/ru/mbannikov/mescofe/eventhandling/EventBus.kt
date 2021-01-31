package ru.mbannikov.mescofe.eventhandling

import ru.mbannikov.mescofe.messaging.MessageSource

interface EventBus : MessageSource<EventMessage<*>> {

    /** Публикует событие в шину. */
    fun publish(eventMessage: EventMessage<*>)

    /** Публикует коллекцию событий в шину. */
    fun publish(eventMessages: List<EventMessage<*>>) {
        eventMessages.forEach(::publish)
    }
}