package com.krishnakandula.reify.state

import java.util.Deque
import java.util.ArrayDeque

object GameStateManager {

    private val states: Deque<GameState> = ArrayDeque<GameState>()
    var depth = 1

    fun push(state: GameState) {
        states.offerFirst(state)
    }

    fun pop(): GameState {
        return states.pollFirst() ?: throw IndexOutOfBoundsException("Deque 'states' is empty")
    }

    fun replace(state: GameState): GameState {
        val s = pop()
        push(state)
        return s
    }

    fun update(deltaTime: Float) {
        for (i in states.size - depth until states.size) {
            states.elementAtOrNull(i)?.update(deltaTime)
        }
    }

    fun render() {
        for (i in states.size - depth until states.size) {
            states.elementAtOrNull(i)?.render()
        }
    }

    fun resize(width: Int, height: Int) {
        for (i in states.size - depth until states.size) {
            states.elementAtOrNull(i)?.resize(width, height)
        }
    }
}
