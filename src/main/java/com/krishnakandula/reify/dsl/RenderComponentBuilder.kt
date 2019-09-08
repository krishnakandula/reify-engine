package com.krishnakandula.reify.dsl

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.krishnakandula.reify.components.RenderComponent

class RenderComponentBuilder(private val texture: Texture) : Builder<RenderComponent> {

    var depth: Short = 0
    var spriteOffsetX = 0f
    var spriteOffsetY = 0f
    var rotation = 0f

    override fun build(): RenderComponent = RenderComponent(
            texture,
            depth,
            Vector2(spriteOffsetX, spriteOffsetY),
            rotation)
}

fun GameObjectBuilder.render(texture: Texture,
                             init: RenderComponentBuilder.() -> Unit): RenderComponent {
    val builder = RenderComponentBuilder(texture)
    builder.init()

    return builder.build()
}
