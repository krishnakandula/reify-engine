package com.krishnakandula.reify.systems

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.components.RenderComponent
import com.krishnakandula.reify.components.TransformComponent

class RenderingSystem(private val spriteBatch: SpriteBatch,
                      private val shapeRenderer: ShapeRenderer,
                      priority: Short = 127) : System(priority) {

    companion object {
        private val componentList = listOf(RenderComponent::class.java, TransformComponent::class.java)
    }

    override fun process(deltaTime: Float, gameObject: GameObject) {
        spriteBatch.begin()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.CORAL

        val renderable = gameObject.getComponent<RenderComponent>() ?: return
        val transform = gameObject.getComponent<TransformComponent>() ?: return
        shapeRenderer.rect(
                transform.position.x,
                transform.position.y,
                transform.width,
                transform.height)

        spriteBatch.end()
        shapeRenderer.end()
    }

    override fun getFilters(): List<Class<out Component>> = componentList

    override fun dispose() {
        spriteBatch.dispose()
        shapeRenderer.dispose()
    }
}
