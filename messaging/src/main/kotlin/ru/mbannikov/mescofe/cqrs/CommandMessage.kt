package ru.mbannikov.mescofe.cqrs

import ru.mbannikov.mescofe.messaging.Message

interface CommandMessage<T : Any> : Message<T>
