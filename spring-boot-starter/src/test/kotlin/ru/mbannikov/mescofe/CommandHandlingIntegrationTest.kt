package ru.mbannikov.mescofe

import mu.KLogger
import mu.KotlinLogging.logger
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import ru.mbannikov.mescofe.TestCommandHandler.Companion.HANDLER_RESULT
import ru.mbannikov.mescofe.cqrs.CommandGateway
import ru.mbannikov.mescofe.cqrs.annotation.CommandHandler

@Import(TestCommandHandler::class)
private class CommandHandlingIntegrationTest @Autowired constructor(
    private val commandGateway: CommandGateway,
    private val commandHandler: TestCommandHandler
) : AbstractSpringBootTest() {

    @Test
    fun `check that the command handler received the same command that was published to command gateway`() {
        publishCommand()
        awaitHandlerToReceiveCommand()
        assertHandlerReceivedSameCommand()
    }

//    @Test
    fun `check that the command handler returns proper result`() {
        // TODO: для прохождения теста необходимо, чтобы AmqpCommandBus::handleMessage начал возвращать CommandMessage<*>
        val commandResult = publishCommandAndWait<String>()
        assert(commandResult == HANDLER_RESULT)
    }

    private fun publishCommand() {
        logger.debug { "Publish command=$REGISTER_USER_COMMAND to the command gateway" }
        commandGateway.send(REGISTER_USER_COMMAND)
    }

    private fun <T: Any> publishCommandAndWait(): T {
        logger.debug { "Publish command=$REGISTER_USER_COMMAND to the command gateway" }
        val commandResult = commandGateway.sendAndWait<T>(REGISTER_USER_COMMAND)
        logger.debug { "Got command result=$commandResult" }

        return commandResult
    }

    private fun awaitHandlerToReceiveCommand() {
        logger.debug { "Wait until the command handler receives published command" }
        await until { commandHandler.receivedCommands.size == 1 }
        logger.debug { "The command handler received published command=${commandHandler.receivedCommands}" }
    }

    private fun assertHandlerReceivedSameCommand() {
        val publishedCommand = REGISTER_USER_COMMAND
        val receivedCommand = commandHandler.receivedCommands.first()

        assert(publishedCommand == receivedCommand)
    }

    companion object {
        private val REGISTER_USER_COMMAND = RegisterUserCommand(username = "user", email = "test@gmail.com")
        private val logger: KLogger = logger {}
    }
}

private class TestCommandHandler {
    val receivedCommands: MutableSet<Any> = mutableSetOf()

    @CommandHandler
    private fun handleRegisterUserCommand(command: RegisterUserCommand): String {
        logger.debug { "The command handler received an command message=$command" }
        receivedCommands.add(command)
        return HANDLER_RESULT
    }

    companion object {
        const val HANDLER_RESULT: String = "SomeCommandResult"
        private val logger: KLogger = logger {}
    }
}

private data class RegisterUserCommand(
    val username: String,
    val email: String
)
