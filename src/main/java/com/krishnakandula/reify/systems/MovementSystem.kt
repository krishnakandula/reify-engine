package com.krishnakandula.reify.systems

import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.Scene
import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.components.MovementComponent
import com.krishnakandula.reify.components.TransformComponent

class MovementSystem(priority: Short = 125) : System(priority = priority) {

    companion object {
        private val componentList = listOf(MovementComponent::class.java, TransformComponent::class.java)
    }

    private var scene: Scene? = null

    override fun onAddedToScene(scene: Scene) {
        super.onAddedToScene(scene)
        this.scene = scene
    }

    override fun onRemovedFromScene() {
        super.onRemovedFromScene()
        this.scene = scene
    }

    override fun fixedUpdate(deltaTime: Float, gameObjects: Collection<GameObject>) {
        super.fixedUpdate(deltaTime, gameObjects)
        gameObjects.forEach { fixedUpdate(deltaTime, it) }
    }

    private fun fixedUpdate(deltaTime: Float, gameObject: GameObject) {
        val transformComponent = scene?.getComponent<TransformComponent>(gameObject) ?: return
        val movementComponent = scene?.getComponent<MovementComponent>(gameObject) ?: return

        transformComponent.position.add(movementComponent.velocity.cpy().scl(deltaTime))
    }

    override fun getFilters(): List<Class<out Component>> = componentList
}
