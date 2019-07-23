package com.krishnakandula.reify.systems

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Rectangle
import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.Scene
import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.components.RenderComponent
import com.krishnakandula.reify.components.TransformComponent
import com.krishnakandula.reify.overlaps

class DebugRenderingSystem(private val shapeRenderer: ShapeRenderer,
                           private val camera: Camera,
                           priority: Short = 127) : System(priority) {

    companion object {
        private val componentList = listOf(RenderComponent::class.java, TransformComponent::class.java)
    }

    private val viewableArea = Rectangle()
    private var scene: Scene? = null

    fun updateProjectionMatrix(projectionMatrix: Matrix4) {
        shapeRenderer.projectionMatrix = projectionMatrix
    }

    override fun onAddedToScene(scene: Scene) {
        super.onAddedToScene(scene)
        this.scene = scene
    }

    override fun onRemovedFromScene() {
        super.onRemovedFromScene()
        this.scene = null
    }

    override fun update(deltaTime: Float, gameObjects: Collection<GameObject>) {
        super.update(deltaTime, gameObjects)

        viewableArea.setX(camera.position.x - (camera.viewportWidth / 2))
        viewableArea.setY(camera.position.y - (camera.viewportHeight / 2))
        viewableArea.setWidth(camera.viewportWidth)
        viewableArea.setHeight(camera.viewportHeight)

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.CORAL

        gameObjects.sortedWith(Comparator { o1, o2 ->
            val r1 = scene?.getComponent<RenderComponent>(o1) ?: return@Comparator 1
            val r2 = scene?.getComponent<RenderComponent>(o2) ?: return@Comparator -1

            return@Comparator r2.depth.compareTo(r1.depth)
        }).filter { gameObject ->
            val transform = scene?.getComponent<TransformComponent>(gameObject) ?: return@filter false
            return@filter viewableArea.overlaps(transform.position.x, transform.position.y, transform.width, transform.height)
        }.forEach(this::update)
        shapeRenderer.end()
    }

    private fun update(gameObject: GameObject) {
        val transform = scene?.getComponent<TransformComponent>(gameObject) ?: return
        shapeRenderer.rect(
                transform.position.x,
                transform.position.y,
                transform.width,
                transform.height)
    }

    override fun getFilters(): List<Class<out Component>> = componentList

    override fun dispose() {
        shapeRenderer.dispose()
    }
}
