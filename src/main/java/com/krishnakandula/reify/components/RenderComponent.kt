package com.krishnakandula.reify.components

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2

class RenderComponent(texture: Texture,
                      val depth: Short = 0,
                      val spriteOffset: Vector2 = Vector2.Zero.cpy()) : Component {

    val sprite = Sprite(texture)
}
