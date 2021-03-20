package ru.mbannikov.mescofe.cqrs

data class GenericCommandResultMessage<T : Any>(
    override val identifier: String,
    override val type: String,
    override val payload: T
) : CommandResultMessage<T>
