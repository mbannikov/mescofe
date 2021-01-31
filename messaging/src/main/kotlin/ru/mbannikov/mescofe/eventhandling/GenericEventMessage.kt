package ru.mbannikov.mescofe.eventhandling

data class GenericEventMessage<T : Any>(
    override val identifier: String,
    override val type: String,
    override val payload: T
) : EventMessage<T>
