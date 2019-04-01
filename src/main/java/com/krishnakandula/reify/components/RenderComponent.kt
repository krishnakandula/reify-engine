package com.krishnakandula.reify.components

import com.badlogic.gdx.graphics.Texture

data class RenderComponent(val texture: Texture,
                           val depth: Byte = Byte.MAX_VALUE) : Component {

    override fun dispose() {
        texture.dispose()
    }
}
