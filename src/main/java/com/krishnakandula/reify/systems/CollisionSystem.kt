package com.krishnakandula.reify.systems

import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.components.CollisionComponent
import com.krishnakandula.reify.components.TransformComponent

class CollisionSystem : System() {

    companion object {
        private val filters = listOf(CollisionComponent::class.java, TransformComponent::class.java)
    }

    override fun process(deltaTime: Float, gameObject: GameObject) {
    }

    override fun getFilters() = filters
}
