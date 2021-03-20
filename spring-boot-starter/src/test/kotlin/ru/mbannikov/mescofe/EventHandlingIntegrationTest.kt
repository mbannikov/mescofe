package ru.mbannikov.mescofe

import mu.KLogger
import mu.KLogging
import mu.KotlinLogging.logger
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Import
import ru.mbannikov.mescofe.eventhandling.EventGateway
import ru.mbannikov.mescofe.eventhandling.annotation.EventHandler
import ru.mbannikov.mescofe.messaging.SimpleMessagePayloadTypeResolver

@Import(TestEventHandler::class, EventHandlingIntegrationTest.MessagePayloadTypeRegister::class)
private class EventHandlingIntegrationTest @Autowired constructor(
    private val eventGateway: EventGateway,
    private val eventHandler: TestEventHandler
) : AbstractSpringBootTest() {

    @Test
    fun `check that the event handler received the same events that were published to event gateway`() {
        publishEvents()
        awaitHandlerToReceiveEvents()
        assertHandlerReceivedSameEvents()
    }

    private fun publishEvents() {
        logger.debug { "Publish events=[$USER_REGISTERED_EVENT, $USER_CHANGED_EVENT] to the event gateway" }
        eventGateway.publish(USER_REGISTERED_EVENT, USER_CHANGED_EVENT)
    }

    private fun awaitHandlerToReceiveEvents() {
        logger.debug { "Wait until the event handler receives all published events" }
        await until { eventHandler.receivedEvents.size == 2 }
        logger.debug { "The event handler received all published events=${eventHandler.receivedEvents}" }
    }

    private fun assertHandlerReceivedSameEvents() {
        val publishedEvents = setOf(USER_REGISTERED_EVENT, USER_CHANGED_EVENT)
        val receivedEvents = eventHandler.receivedEvents

        assert(publishedEvents == receivedEvents)
    }

    class MessagePayloadTypeRegister(
        @Qualifier("eventPayloadTypeResolver") private val eventPayloadTypeResolver: SimpleMessagePayloadTypeResolver,
    ) : ApplicationListener<ApplicationReadyEvent> {
        override fun onApplicationEvent(event: ApplicationReadyEvent) {
            eventPayloadTypeResolver.registerPayloadType("UserRegisteredEvent", UserRegisteredEvent::class)
            eventPayloadTypeResolver.registerPayloadType("UserChangedEvent", UserChangedEvent::class)
        }
    }

    companion object {
        private val USER_REGISTERED_EVENT = UserRegisteredEvent(userId = "1", userEmail = "test@gmail.com")
        private val USER_CHANGED_EVENT = UserChangedEvent(newEmail = "test@protonmail.com")
        private val logger: KLogger = logger {}
    }
}

private class TestEventHandler {
    val receivedEvents: MutableSet<Any> = mutableSetOf()

    @EventHandler
    private fun handleUserRegisteredEvent(event: UserRegisteredEvent) = handle(event)

    @EventHandler
    private fun handleUserChangedEvent(event: UserChangedEvent) = handle(event)

    private fun handle(event: Any) {
        logger.debug { "The event handler received an event message=$event" }
        receivedEvents.add(event)
    }

    companion object: KLogging()
}

private data class UserRegisteredEvent(
    val userId: String,
    val userEmail: String
)

private data class UserChangedEvent(
    val newEmail: String
)
