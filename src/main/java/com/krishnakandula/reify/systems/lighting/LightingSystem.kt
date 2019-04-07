package com.krishnakandula.reify.systems.lighting

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.Viewport
import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.components.LightingComponent
import com.krishnakandula.reify.components.TransformComponent
import com.krishnakandula.reify.systems.System
import com.krishnakandula.reify.systems.lighting.lights.RayHandler

class LightingSystem(world: World,
                     private val camera: OrthographicCamera,
                     private val viewport: Viewport) : System() {

    companion object {
        private val componentList = listOf(LightingComponent::class.java)
    }

    private val rayHandler = RayHandler(world)

    override fun getFilters(): List<Class<out Component>> = componentList

    override fun update(deltaTime: Float, gameObjects: Collection<GameObject>) {
        super.update(deltaTime, gameObjects)

        gameObjects.forEach(this::updateLightPosition)
        val lights = gameObjects.mapNotNull { gameObject -> gameObject.getComponent<LightingComponent>() }
                .filter(LightingComponent::enabled)
                .map(LightingComponent::light)

        rayHandler.setCombinedMatrix(camera)
        rayHandler.updateAndRender(lights)
    }

    private fun updateLightPosition(gameObject: GameObject) {
        val transform = gameObject.getComponent<TransformComponent>() ?: return
        val lighting = gameObject.getComponent<LightingComponent>() ?: return

        lighting.light.setPosition(
                transform.position.x + (transform.width / 2),
                transform.position.y + (transform.height / 2))
    }

    override fun resize(width: Float, height: Float) {
        super.resize(width, height)

        val gutterWidth = viewport.leftGutterWidth
        val gutterHeight = viewport.topGutterHeight
        val rhWidth = width - (2 * gutterWidth)
        val rhHeight = height - (2 * gutterHeight)

        rayHandler.useCustomViewport(gutterWidth, gutterHeight, rhWidth.toInt(), rhHeight.toInt())
    }

    override fun dispose() {
        super.dispose()
        rayHandler.dispose()
    }
}
