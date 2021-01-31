package ru.mbannikov.mescofe.eventhandling

import ru.mbannikov.mescofe.messaging.Message

interface EventMessage<T : Any> : Message<T>
