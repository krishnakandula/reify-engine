package com.krishnakandula.reify.systems

import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.components.Component

abstract class System(val priority: Short = 1,
                      var enabled: Boolean = true) {

    abstract fun process(deltaTime: Float, gameObject: GameObject)

    abstract fun getFilters(): List<Class<out Component>>

    open fun dispose() { }
}
