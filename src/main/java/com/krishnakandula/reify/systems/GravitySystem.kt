package com.krishnakandula.reify.systems

import com.badlogic.gdx.math.Vector2
import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.components.GravityComponent
import com.krishnakandula.reify.components.MovementComponent

class GravitySystem(gravity: Float = -98f,
                    priority: Short = 1) : System(priority = priority) {

    companion object {
        private val componentList = listOf(GravityComponent::class.java, MovementComponent::class.java)
    }

    private val gravityVector = Vector2(0f, gravity)

    override fun fixedUpdate(deltaTime: Float, gameObjects: Collection<GameObject>) {
        gameObjects.forEach { this.fixedUpdate(deltaTime, it) }
    }

    private fun fixedUpdate(deltaTime: Float, gameObject: GameObject) {
        val movementComponent = gameObject.getComponent<MovementComponent>() ?: return
        movementComponent.velocity.add(gravityVector.cpy().scl(deltaTime))
    }

    override fun getFilters(): List<Class<out Component>> = componentList
}
