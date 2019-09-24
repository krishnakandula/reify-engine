package com.krishnakandula.reify.dsl.systems

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.krishnakandula.reify.Scene
import com.krishnakandula.reify.systems.DebugRenderingSystem

class DebugRenderingSystemBuilder(private val shapeRenderer: ShapeRenderer,
                                  private val camera: Camera) : SystemBuilder<DebugRenderingSystem>() {

    override fun build(): DebugRenderingSystem = DebugRenderingSystem(shapeRenderer, camera, priority)
}

fun Scene.debugRenderer(shapeRenderer: ShapeRenderer,
                        camera: Camera,
                        init: DebugRenderingSystemBuilder.() -> Unit): DebugRenderingSystem {

    val builder = DebugRenderingSystemBuilder(shapeRenderer, camera)
    builder.init()
    val system = builder.build()
    addSystem(system)

    return system
}
