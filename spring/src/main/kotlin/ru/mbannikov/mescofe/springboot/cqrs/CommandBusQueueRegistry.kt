package ru.mbannikov.mescofe.springboot.cqrs

import org.springframework.amqp.core.Queue

class CommandBusQueueRegistry {
    private val queueRegistry: MutableList<Queue> = mutableListOf()

    val queueNames: Array<String>
        get() = queueRegistry.map { it.name }.toTypedArray()

    fun register(queue: Queue) {
        queueRegistry.add(queue)
    }
}