package ru.mbannikov.mescofe.messaging

import java.io.Serializable

interface Message<T : Any> : Serializable {

    /** Уникальный идентификатор сообщения. */
    val identifier: String

    /** Тип полезной нагрузки сообщения. */
    val type: String

    /** Полезная нагрузка сожержащаяся в сообщении. */
    val payload: T
}