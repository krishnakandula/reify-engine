package com.krishnakandula.reify.dsl.components

import com.badlogic.gdx.math.Vector2
import com.krishnakandula.reify.components.TransformComponent
import com.krishnakandula.reify.dsl.GameObjectBuilder
import com.krishnakandula.reify.dsl.ReifyDsl

@ReifyDsl
class TransformComponentBuilder(var position: Vector2 = Vector2.Zero,
                                var width: Float = 0f,
                                var height: Float = 0f) {

    fun build() = TransformComponent(position, width, height)
}

fun GameObjectBuilder.transform(setup: TransformComponentBuilder.() -> Unit): TransformComponent {
    val builder = TransformComponentBuilder()
    builder.setup()

    return builder.build()
}
