package ru.mbannikov.mescofe

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(initializers = [TestContainerInitializer::class])
abstract class AbstractSpringBootTest