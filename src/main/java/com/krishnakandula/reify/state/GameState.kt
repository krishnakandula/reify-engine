package com.krishnakandula.reify.state

abstract class GameState {

    abstract fun update(deltaTime: Float)
    abstract fun render()
    abstract fun resize(width: Int, height: Int)
    abstract fun dispose()
}
