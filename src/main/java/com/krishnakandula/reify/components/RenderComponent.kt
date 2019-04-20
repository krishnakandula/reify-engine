package com.krishnakandula.reify.components

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2

data class RenderComponent(private val texture: Texture,
                           val depth: Byte = Byte.MAX_VALUE,
                           val spriteOffset: Vector2 = Vector2.Zero) : Component {

    val sprite = Sprite(texture)
}
