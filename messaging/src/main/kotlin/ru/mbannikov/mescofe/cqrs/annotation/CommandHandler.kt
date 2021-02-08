package ru.mbannikov.mescofe.cqrs.annotation

import ru.mbannikov.mescofe.cqrs.CommandMessage
import ru.mbannikov.mescofe.messaging.annotation.MessageHandler

@MessageHandler(messageType = CommandMessage::class)
annotation class CommandHandler
