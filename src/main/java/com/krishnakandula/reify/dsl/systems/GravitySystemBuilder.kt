package com.krishnakandula.reify.dsl.systems

import com.krishnakandula.reify.Scene
import com.krishnakandula.reify.systems.GravitySystem

class GravitySystemBuilder : SystemBuilder<GravitySystem>() {

    var gravity = -9.8f

    override fun build(): GravitySystem = GravitySystem(gravity, priority)
}

fun Scene.gravitySystem(init: GravitySystemBuilder.() -> Unit): GravitySystem {
    val builder = GravitySystemBuilder()
    builder.init()
    val system = builder.build()
    this.addSystem(system)
    return system
}
