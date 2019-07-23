package com.krishnakandula.reify.systems.lighting

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.Viewport
import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.Scene
import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.components.LightingComponent
import com.krishnakandula.reify.components.TransformComponent
import com.krishnakandula.reify.systems.System
import com.krishnakandula.reify.systems.lighting.lights.RayHandler

class LightingSystem(world: World,
                     private val camera: OrthographicCamera,
                     private val viewport: Viewport,
                     priority: Short = 128) : System(priority) {

    companion object {
        private val componentList = listOf(LightingComponent::class.java)
    }

    private val rayHandler = RayHandler(world)
    private var scene: Scene? = null

    fun useDiffuseLighting(useDiffuse: Boolean) {
        RayHandler.isDiffuse = useDiffuse
    }

    fun setAmbientLighting(r: Float, g: Float, b: Float, a: Float) {
        rayHandler.setAmbientLight(r, g, b, a)
    }

    fun useBlur(useBlur: Boolean, blurNum: Int = 1) {
        rayHandler.setBlur(useBlur)
        if (useBlur) {
            rayHandler.setBlurNum(blurNum)
        }
    }

    fun setCulling(useCulling: Boolean) {
        rayHandler.setCulling(useCulling)
    }

    fun useGammaCorrection(useGammaCorrection: Boolean) {
        RayHandler.setGammaCorrection(useGammaCorrection)
    }

    fun useShadows(useShadows: Boolean) {
        rayHandler.setShadows(useShadows)
    }

    override fun onAddedToScene(scene: Scene) {
        super.onAddedToScene(scene)
        this.scene = scene
    }

    override fun onRemovedFromScene() {
        super.onRemovedFromScene()
        this.scene = null
    }

    override fun getFilters(): List<Class<out Component>> = componentList

    override fun update(deltaTime: Float, gameObjects: Collection<GameObject>) {
        super.update(deltaTime, gameObjects)

        gameObjects.forEach(this::updateLightPosition)
        val lights = gameObjects.mapNotNull { gameObject -> scene?.getComponent<LightingComponent>(gameObject) }
                .filter(LightingComponent::enabled)
                .map(LightingComponent::light)

        rayHandler.setCombinedMatrix(camera)
        rayHandler.updateAndRender(lights)
    }

    private fun updateLightPosition(gameObject: GameObject) {
        val transform = scene?.getComponent<TransformComponent>(gameObject) ?: return
        val lighting = scene?.getComponent<LightingComponent>(gameObject) ?: return

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
