package com.krishnakandula.reify

import com.krishnakandula.reify.components.Component
import java.util.UUID

class GameObject(val tag: String = "",
                 val components: MutableMap<Class<out Component>, Component> = HashMap()) {

    val id = UUID.randomUUID().toString()

    inline fun <reified T : Component> addComponent(component: T) {
        components.putIfAbsent(T::class.java, component)
    }

    inline fun <reified T : Component> removeComponent() {
        if (components.containsKey(T::class.java)) {
            components.remove(T::class.java)
        }
    }

    inline fun <reified T : Component> getComponent(): T? {
        return if (components.containsKey(T::class.java)) components[T::class.java] as T else null
    }

    inline fun <reified T : Component> hasComponent(): Boolean = components.containsKey(T::class.java)

    fun getComponents(): List<Component> = components.values.toList()

    open fun dispose() {
        components.values.forEach(Component::dispose)
    }
}
