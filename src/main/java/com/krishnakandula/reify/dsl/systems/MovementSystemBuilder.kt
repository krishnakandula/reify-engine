package com.krishnakandula.reify.dsl.systems

import com.krishnakandula.reify.Scene
import com.krishnakandula.reify.systems.MovementSystem

class MovementSystemBuilder : SystemBuilder<MovementSystem>() {

    override fun build(): MovementSystem = MovementSystem(priority)
}

fun Scene.movementSystem(init: MovementSystemBuilder.() -> Unit): MovementSystem {
    val builder = MovementSystemBuilder()
    builder.init()
    val system = builder.build()
    this.addSystem(system)
    return system
}
