package com.krishnakandula.reify.dsl.components

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.krishnakandula.reify.components.RenderComponent
import com.krishnakandula.reify.dsl.ReifyDsl

@ReifyDsl
class RenderComponentBuilder {
    private lateinit var texture: Texture
    private var depth = Byte.MAX_VALUE
    private var rotation = 0f
    private var spriteOffset: Vector2 = Vector2.Zero.cpy()

    fun build() = RenderComponent(texture, depth, spriteOffset, rotation)
}


