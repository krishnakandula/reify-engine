package com.krishnakandula.reify.systems

import box2dLight.RayHandler
import com.badlogic.gdx.physics.box2d.World
import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.components.LightingComponent

class LightingSystem(world: World) : System() {

    companion object {
        private val componentList = listOf(LightingComponent::class.java)
    }

    private val rayHandler = RayHandler(world)

    init {
        rayHandler.setShadows(false)
    }

    override fun getFilters(): List<Class<out Component>> = componentList

    override fun dispose() {
        super.dispose()
        rayHandler.dispose()
    }
}
