package com.krishnakandula.reify.dsl

import com.badlogic.gdx.graphics.Color
import com.krishnakandula.reify.components.DebugRenderComponent

class DebugRenderComponentBuilder : Builder<DebugRenderComponent> {

    var color = Color.CORAL
    var shape = DebugRenderComponent.Shape.RECT

    override fun build(): DebugRenderComponent = DebugRenderComponent(color, shape)
}

fun GameObjectBuilder.debugRender(init: DebugRenderComponentBuilder.() -> Unit): DebugRenderComponent {
    val builder = DebugRenderComponentBuilder()
    builder.init()
    val component = builder.build()
    this.components.add(component)

    return component
}
