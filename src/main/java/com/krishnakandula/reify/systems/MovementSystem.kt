package com.krishnakandula.reify.systems

import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.components.MovementComponent
import com.krishnakandula.reify.components.TransformComponent

class MovementSystem(priority: Short = 125) : System(priority = priority) {

    companion object {
        private val componentList = listOf(MovementComponent::class.java, TransformComponent::class.java)
    }

    override fun fixedUpdate(deltaTime: Float, gameObject: GameObject) {
        val transformComponent = gameObject.getComponent<TransformComponent>() ?: return
        val movementComponent = gameObject.getComponent<MovementComponent>() ?: return

        transformComponent.position.add(movementComponent.velocity.cpy().scl(deltaTime))
    }

    override fun getFilters(): List<Class<out Component>> = componentList
}
