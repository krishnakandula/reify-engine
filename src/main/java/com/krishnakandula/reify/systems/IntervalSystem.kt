package com.krishnakandula.reify.systems

import com.krishnakandula.reify.GameObject

abstract class IntervalSystem(priority: Short = 127,
                              private val fixedInterval: Float = DEFAULT_INTERVAL) : System(priority) {

    companion object {
        private const val DEFAULT_INTERVAL = 1f/60f
    }

    private var accumulator = 0f

    override fun process(deltaTime: Float, gameObject: GameObject) {
        accumulator += deltaTime
        while (accumulator >= fixedInterval) {
            fixedUpdate(gameObject)
            accumulator -= fixedInterval
        }
    }

    abstract fun fixedUpdate(gameObject: GameObject)
}
