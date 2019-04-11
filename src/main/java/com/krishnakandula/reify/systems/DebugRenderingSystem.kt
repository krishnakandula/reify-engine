package com.krishnakandula.reify.systems

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.components.RenderComponent
import com.krishnakandula.reify.components.TransformComponent

class DebugRenderingSystem(private val shapeRenderer: ShapeRenderer,
                           priority: Short = 127) : System(priority) {

    companion object {
        private val componentList = listOf(RenderComponent::class.java, TransformComponent::class.java)
    }

    override fun update(deltaTime: Float, gameObjects: Collection<GameObject>) {
        super.update(deltaTime, gameObjects)

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.CORAL

        gameObjects.sortedWith(Comparator { o1, o2 ->
            val r1 = o1.getComponent<RenderComponent>() ?: return@Comparator 1
            val r2 = o2.getComponent<RenderComponent>() ?: return@Comparator -1

            return@Comparator r2.depth.compareTo(r1.depth)
        }).forEach(this::update)
        shapeRenderer.end()
    }

    private fun update(gameObject: GameObject) {
        val transform = gameObject.getComponent<TransformComponent>() ?: return

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
