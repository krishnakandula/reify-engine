package com.krishnakandula.reify.dsl.systems

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.krishnakandula.reify.Scene
import com.krishnakandula.reify.systems.RenderingSystem

class RenderingSystemBuilder(private val spriteBatch: SpriteBatch,
                             private val camera: Camera) : SystemBuilder<RenderingSystem>() {

    override fun build(): RenderingSystem = RenderingSystem(spriteBatch, camera, priority)
}

fun Scene.renderingSystem(spriteBatch: SpriteBatch,
                          camera: Camera,
                          init: RenderingSystemBuilder.() -> Unit): RenderingSystem {
    val builder = RenderingSystemBuilder(spriteBatch, camera)
    builder.init()
    val system = builder.build()
    this.addSystem(system)

    return system
}
