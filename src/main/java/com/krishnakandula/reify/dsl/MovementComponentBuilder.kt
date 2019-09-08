package com.krishnakandula.reify.dsl

import com.badlogic.gdx.math.Vector2
import com.krishnakandula.reify.components.MovementComponent

class MovementComponentBuilder : Builder<MovementComponent> {

    var velocityX = 0f
    var velocityY = 0f

    override fun build(): MovementComponent = MovementComponent(Vector2(velocityX, velocityY))
}

fun GameObjectBuilder.movement(init: MovementComponentBuilder.() -> Unit): MovementComponent {
    val builder = MovementComponentBuilder()
    builder.init()

    return builder.build()
}
