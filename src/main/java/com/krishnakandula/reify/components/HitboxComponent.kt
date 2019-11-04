package com.krishnakandula.reify.components

import com.badlogic.gdx.math.Shape2D

data class HitboxComponent(var shape: Shape2D,
                           var offsetX: Float,
                           var offsetY: Float,
                           var collisionFilter: Int = CollisionFilter.ALL) : Component

object CollisionFilter {

    const val NONE = 0
    const val ALL = -1
}
