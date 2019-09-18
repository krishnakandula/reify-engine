package com.krishnakandula.reify.dsl

import com.badlogic.gdx.math.Vector2
import com.krishnakandula.reify.components.TransformComponent

class TransformComponentBuilder : Builder<TransformComponent> {

    var x = 0f
    var y = 0f
    var width = 0f
    var height = 0f
    var rotation = 0f

    override fun build(): TransformComponent = TransformComponent(
            Vector2(x, y),
            width,
            height,
            rotation)
}

fun GameObjectBuilder.transform(init: TransformComponentBuilder.() -> Unit): TransformComponent {
    val transformComponentBuilder = TransformComponentBuilder()
    transformComponentBuilder.init()

    return transformComponentBuilder.build()
}
