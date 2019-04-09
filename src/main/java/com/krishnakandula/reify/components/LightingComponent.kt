package com.krishnakandula.reify.components

import com.krishnakandula.reify.systems.lighting.lights.Light

data class LightingComponent(val light: Light,
                             var enabled: Boolean) : Component {

    override fun dispose() {
        super.dispose()
        light.dispose()
    }
}
