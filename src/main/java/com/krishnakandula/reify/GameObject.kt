package com.krishnakandula.reify

import com.badlogic.gdx.math.Vector2
import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.components.TransformComponent
import java.util.UUID

abstract class GameObject(positionX: Float = 0f,
                          positionY: Float = 0f,
                          width: Float = 1f,
                          height: Float = 1f,
                          val tag: String = "") {

    val id = UUID.randomUUID().toString()
    val components: MutableMap<Class<out Component>, Component> = HashMap()

    init {
        val transform = TransformComponent(Vector2(positionX, positionY), width, height)
        addComponent(transform)
    }

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
