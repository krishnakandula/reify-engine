package com.krishnakandula.reify.components

import com.badlogic.gdx.graphics.Texture

data class RenderComponent(val texture: Texture) : Component {

    override fun dispose() {
        texture.dispose()
    }
}
