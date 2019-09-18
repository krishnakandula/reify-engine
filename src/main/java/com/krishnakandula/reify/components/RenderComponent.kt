package com.krishnakandula.reify.components

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2

data class RenderComponent(private val texture: Texture,
                           val depth: Short = 0,
                           val spriteOffset: Vector2 = Vector2.Zero.cpy(),
                           var rotation: Float = 0f) : Component {

    val sprite = Sprite(texture)
}
