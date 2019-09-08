package com.krishnakandula.reify.dsl

import com.krishnakandula.reify.components.LightingComponent
import com.krishnakandula.reify.systems.lighting.lights.Light

class LightingComponentBuilder(private val light: Light) : Builder<LightingComponent> {

    var enabled = true

    override fun build(): LightingComponent = LightingComponent(light, enabled)
}

fun GameObjectBuilder.lighting(light: Light,
                               init: LightingComponentBuilder.() -> Unit): LightingComponent {
    val builder = LightingComponentBuilder(light)
    builder.init()

    return builder.build()
}
