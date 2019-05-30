package com.krishnakandula.reify.dsl

import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.components.Component

@ReifyDsl
class GameObjectBuilder(private val components: List<Component> = mutableListOf(),
                        var tag: String = "") {

    fun build() = GameObject(tag, createComponentMap())

    private fun createComponentMap(): MutableMap<Class<out Component>, Component> {
        return components.map { component ->
            component.javaClass to component
        }.toMap().toMutableMap()
    }
}
