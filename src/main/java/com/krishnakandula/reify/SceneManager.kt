package com.krishnakandula.reify

import java.util.Deque
import java.util.ArrayDeque

object SceneManager {

    private const val FIXED_INTERVAL = 1f / 100f

    var depth = 1

    private var accumulator = 0f
    private val gameStates: Deque<Scene> = ArrayDeque<Scene>()

    fun push(state: Scene) {
        gameStates.offerFirst(state)
    }

    fun pop(): Scene {
        return gameStates.pollFirst() ?: throw IndexOutOfBoundsException("Deque 'gameStates' is empty")
    }

    fun replace(state: Scene): Scene {
        val s = pop()
        push(state)
        return s
    }

    fun update(deltaTime: Float) {
        accumulator += deltaTime
        while (accumulator >= FIXED_INTERVAL) {
            fixedUpdate(FIXED_INTERVAL)
            accumulator -= FIXED_INTERVAL
        }
        for (i in gameStates.size - depth until gameStates.size) {
            gameStates.elementAtOrNull(i)?.update(deltaTime)
        }
    }

    private fun fixedUpdate(deltaTime: Float) {
        for (i in gameStates.size - depth until gameStates.size) {
            gameStates.elementAtOrNull(i)?.fixedUpdate(deltaTime)
        }
    }

    fun resize(width: Int, height: Int) {
        for (i in gameStates.size - depth until gameStates.size) {
            gameStates.elementAtOrNull(i)?.resize(width.toFloat(), height.toFloat())
        }
    }
}
