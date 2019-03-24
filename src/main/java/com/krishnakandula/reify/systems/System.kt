package com.krishnakandula.reify.systems

import com.krishnakandula.reify.Engine
import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.components.Component

abstract class System(val priority: Short = 1,
                      var enabled: Boolean = true) {

    open fun onAddedToEngine(engine: Engine) { }

    open fun onRemovedFromEngine() { }

    open fun onStartProcessing(deltaTime: Float, engine: Engine) { }

    abstract fun process(deltaTime: Float, gameObject: GameObject)

    abstract fun getFilters(): List<Class<out Component>>

    open fun dispose() { }
}
