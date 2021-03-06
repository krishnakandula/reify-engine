package com.krishnakandula.reify.systems

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Rectangle
import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.components.RenderComponent
import com.krishnakandula.reify.components.TransformComponent
import com.krishnakandula.reify.overlaps

class RenderingSystem(private val spriteBatch: SpriteBatch,
                      private val camera: Camera,
                      priority: Short = 127) : System(priority) {

    companion object {
        private val componentList = listOf(RenderComponent::class.java, TransformComponent::class.java)
    }

    private val viewableArea = Rectangle()

    fun updateProjectionMatrix(projectionMatrix: Matrix4) {
        spriteBatch.projectionMatrix = projectionMatrix
    }

    override fun update(deltaTime: Float, gameObjects: Collection<GameObject>) {
        super.update(deltaTime, gameObjects)

        viewableArea.setX(camera.position.x - (camera.viewportWidth / 2))
        viewableArea.setY(camera.position.y - (camera.viewportHeight / 2))
        viewableArea.setWidth(camera.viewportWidth)
        viewableArea.setHeight(camera.viewportHeight)

        spriteBatch.begin()

        gameObjects.sortedWith(Comparator { o1, o2 ->
            val r1 = scene?.getComponent<RenderComponent>(o1) ?: return@Comparator 1
            val r2 = scene?.getComponent<RenderComponent>(o2) ?: return@Comparator -1

            return@Comparator r1.depth.compareTo(r2.depth)
        }).filter { gameObject ->
            val transform = scene?.getComponent<TransformComponent>(gameObject) ?: return@filter false
            return@filter viewableArea.overlaps(transform.position.x, transform.position.y, transform.width, transform.height)
        }.forEach(this::update)
        spriteBatch.end()
    }

    private fun update(gameObject: GameObject) {
        val renderable = scene?.getComponent<RenderComponent>(gameObject) ?: return
        val transform = scene?.getComponent<TransformComponent>(gameObject) ?: return

        val spritePositionX = transform.position.x + renderable.spriteOffset.x
        val spritePositionY = transform.position.y + renderable.spriteOffset.y

        renderable.sprite.setOrigin(0f, 0f)
        renderable.sprite.setScale(transform.height / renderable.sprite.height)
        renderable.sprite.setPosition(spritePositionX, spritePositionY)
        renderable.sprite.rotation = transform.rotation
        renderable.sprite.draw(spriteBatch)
    }

    override fun getFilters(): List<Class<out Component>> = componentList

    override fun dispose() {
        spriteBatch.dispose()
    }
}
