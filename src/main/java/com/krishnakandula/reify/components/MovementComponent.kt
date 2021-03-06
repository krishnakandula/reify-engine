package com.krishnakandula.reify.components

import com.badlogic.gdx.math.Vector2

data class MovementComponent(val velocity: Vector2,
                             val rotation: Float = 0f) : Component
