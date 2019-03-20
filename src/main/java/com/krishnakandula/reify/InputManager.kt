package com.krishnakandula.reify

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

object InputManager : InputAdapter() {
    private val LOG_TAG = InputManager::class.java.simpleName
    private val inputPublisher = PublishSubject.create<Input>()

    override fun keyDown(keycode: Int): Boolean {
        Gdx.app.log(LOG_TAG, "Key Down: $keycode")
        inputPublisher.onNext(InputManager.Input.KeyDown(keycode))
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        Gdx.app.log(LOG_TAG, "Key Up: $keycode")
        inputPublisher.onNext(InputManager.Input.KeyUp(keycode))
        return true
    }

    fun getInputPublisher(): Observable<Input> = inputPublisher

    sealed class Input {

        data class KeyDown(val keycode: Int) : Input()

        data class KeyUp(val keycode: Int) : Input()
    }
}
