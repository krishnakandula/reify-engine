package com.krishnakandula.reify.systems

import com.badlogic.gdx.math.Vector2
import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.components.GravityComponent
import com.krishnakandula.reify.components.MovementComponent

class GravitySystem(gravity: Float = -98f,
                    priority: Short = 1) : IntervalSystem(priority = priority) {

    companion object {
        private val componentList = listOf(GravityComponent::class.java, MovementComponent::class.java)
    }

    private val gravityVector = Vector2(0f, gravity)

//    override fun process(deltaTime: Float, gameObject: GameObject) {
//        val movementComponent = gameObject.getComponent<MovementComponent>()!!
//        movementComponent.velocity.add(gravityVector)
//    }

    override fun fixedUpdate(gameObject: GameObject) {
        val movementComponent = gameObject.getComponent<MovementComponent>()!!
        movementComponent.velocity.add(gravityVector)    }

    override fun getFilters(): List<Class<out Component>> = componentList
}
