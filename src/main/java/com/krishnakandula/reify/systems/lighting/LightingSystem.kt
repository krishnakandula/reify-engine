package com.krishnakandula.reify.systems.lighting

import com.badlogic.gdx.physics.box2d.World
import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.components.LightingComponent
import com.krishnakandula.reify.systems.System
import com.krishnakandula.reify.systems.lighting.lights.RayHandler

class LightingSystem(world: World) : System() {

    companion object {
        private val componentList = listOf(LightingComponent::class.java)
    }

    private val rayHandler = RayHandler(world)

    override fun getFilters(): List<Class<out Component>> = componentList

    override fun update(deltaTime: Float, gameObjects: Collection<GameObject>) {
        super.update(deltaTime, gameObjects)

        rayHandler.removeAllAndDispose()
    }
}
