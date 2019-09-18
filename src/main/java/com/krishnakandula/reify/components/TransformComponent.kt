package com.krishnakandula.reify.components

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

data class TransformComponent(val position: Vector2,
                              val width: Float,
                              val height: Float,
                              val rotation: Float = 0f) : Component {

    fun getRect(): Rectangle = Rectangle(position.x, position.y, width, height)
}
