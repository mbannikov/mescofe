package ru.mbannikov.mescofe.utils

import java.lang.reflect.Method
import java.util.LinkedList

object ReflectionUtils {
    fun methodsOf(clazz: Class<*>): Iterable<Method> {
        val methods: MutableList<Method> = LinkedList()

        var currentClazz: Class<*>? = clazz
        while (currentClazz != null) {
            val classMethods: Iterable<Method> = listOf(*currentClazz.declaredMethods)
            val interfaceMethods: Iterable<Method> = getMethodsOnDeclaredInterfaces(clazz)

            methods.addAll(classMethods)
            methods.addAll(interfaceMethods)

            currentClazz = currentClazz.superclass
        }

        return methods
    }

    private fun getMethodsOnDeclaredInterfaces(clazz: Class<*>): Iterable<Method> =
        clazz.interfaces.flatMap { listOf(*it.declaredMethods) + getMethodsOnDeclaredInterfaces(it) }
}