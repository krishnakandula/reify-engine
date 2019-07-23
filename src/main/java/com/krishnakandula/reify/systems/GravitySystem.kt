package com.krishnakandula.reify.systems

import com.badlogic.gdx.math.Vector2
import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.Scene
import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.components.GravityComponent
import com.krishnakandula.reify.components.MovementComponent

class GravitySystem(gravity: Float = -98f,
                    priority: Short = 1) : System(priority = priority) {

    companion object {
        private val componentList = listOf(GravityComponent::class.java, MovementComponent::class.java)
    }

    private val gravityVector = Vector2(0f, gravity)
    private var scene: Scene? = null

    override fun onAddedToScene(scene: Scene) {
        super.onAddedToScene(scene)
        this.scene = scene
    }

    override fun onRemovedFromScene() {
        super.onRemovedFromScene()
        this.scene = null
    }

    override fun fixedUpdate(deltaTime: Float, gameObjects: Collection<GameObject>) {
        gameObjects.forEach { this.fixedUpdate(deltaTime, it) }
    }

    private fun fixedUpdate(deltaTime: Float, gameObject: GameObject) {
        val movementComponent = scene?.getComponent<MovementComponent>(gameObject) ?: return
        movementComponent.velocity.add(gravityVector.cpy().scl(deltaTime))
    }

    override fun getFilters(): List<Class<out Component>> = componentList
}
