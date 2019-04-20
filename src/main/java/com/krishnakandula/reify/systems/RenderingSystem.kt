package com.krishnakandula.reify.systems

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Matrix4
import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.components.RenderComponent
import com.krishnakandula.reify.components.TransformComponent

class RenderingSystem(private val spriteBatch: SpriteBatch,
                      priority: Short = 127) : System(priority) {

    companion object {
        private val componentList = listOf(RenderComponent::class.java, TransformComponent::class.java)
    }

    fun updateProjectionMatrix(projectionMatrix: Matrix4) {
        spriteBatch.projectionMatrix = projectionMatrix
    }

    override fun update(deltaTime: Float, gameObjects: Collection<GameObject>) {
        super.update(deltaTime, gameObjects)

        spriteBatch.begin()

        gameObjects.sortedWith(Comparator { o1, o2 ->
            val r1 = o1.getComponent<RenderComponent>() ?: return@Comparator 1
            val r2 = o2.getComponent<RenderComponent>() ?: return@Comparator -1

            return@Comparator r2.depth.compareTo(r1.depth)
        }).forEach(this::update)
        spriteBatch.end()
    }

    private fun update(gameObject: GameObject) {
        val renderable = gameObject.getComponent<RenderComponent>() ?: return
        val transform = gameObject.getComponent<TransformComponent>() ?: return

        val spritePositionX = transform.position.x + renderable.spriteOffset.x
        val spritePositionY = transform.position.y + renderable.spriteOffset.y

        renderable.sprite.setPosition(spritePositionX, spritePositionY)
        renderable.sprite.draw(spriteBatch)
    }

    override fun getFilters(): List<Class<out Component>> = componentList

    override fun dispose() {
        spriteBatch.dispose()
    }
}
