package ru.mbannikov.mescofe.cqrs

data class GenericCommandMessage<T : Any>(
    override val identifier: String,
    override val type: String,
    override val payload: T
) : CommandMessage<T>
