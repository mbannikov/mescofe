package ru.mbannikov.mescofe.eventhandling.annotation

import ru.mbannikov.mescofe.eventhandling.EventMessage
import ru.mbannikov.mescofe.messaging.annotation.MessageHandler

@MessageHandler(messageType = EventMessage::class)
annotation class EventHandler
