package com.krishnakandula.reify.dsl.components

import com.badlogic.gdx.math.Vector2
import com.krishnakandula.reify.components.MovementComponent
import com.krishnakandula.reify.dsl.GameObjectBuilder
import com.krishnakandula.reify.dsl.ReifyDsl

@ReifyDsl
class MovementComponentBuilder(var velocity: Vector2 = Vector2.Zero) {
    fun build() = MovementComponent(velocity)
}

fun GameObjectBuilder.movement(setup: MovementComponentBuilder.() -> Unit) {
    val builder = MovementComponentBuilder()
    builder.setup()
}
