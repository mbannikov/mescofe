package ru.mbannikov.mescofe.cqrs

import ru.mbannikov.mescofe.messaging.Message

interface CommandResultMessage<T : Any> : Message<T>
