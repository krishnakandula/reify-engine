package com.krishnakandula.reify.components

import com.badlogic.gdx.graphics.Color

class DebugRenderComponent(val color: Color?, val shape: Shape) : Component {

    enum class Shape {
        RECT, CIRCLE, TRIANGLE
    }
}
