package com.krishnakandula.reify.components

import com.badlogic.gdx.math.Vector2

data class TransformComponent(val position: Vector2,
                              val scale: Vector2) : Component
