package com.krishnakandula.reify.dsl

import com.badlogic.gdx.math.Shape2D
import com.krishnakandula.reify.components.CollisionFilter
import com.krishnakandula.reify.components.HitboxComponent

class HitBoxComponentBuilder(private val shape2D: Shape2D) : Builder<HitboxComponent> {

    var offsetX = 0f
    var offsetY = 0f
    var collisionFilter = CollisionFilter.ALL

    override fun build(): HitboxComponent = HitboxComponent(shape2D, offsetX, offsetY, collisionFilter)
}

fun GameObjectBuilder.hitBox(shape2D: Shape2D,
                             init: HitBoxComponentBuilder.() -> Unit): HitboxComponent {
    val builder = HitBoxComponentBuilder(shape2D)
    builder.init()

    return builder.build()
}
