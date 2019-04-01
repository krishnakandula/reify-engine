package com.krishnakandula.reify.systems

import com.krishnakandula.reify.Engine
import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.components.Component

abstract class System(val priority: Short = 1,
                      var enabled: Boolean = true) {

    open fun onAddedToEngine(engine: Engine) { }

    open fun onRemovedFromEngine() { }

    open fun update(deltaTime: Float, gameObjects: Collection<GameObject>) { }

    open fun fixedUpdate(deltaTime: Float, gameObjects: Collection<GameObject>) { }

    abstract fun getFilters(): List<Class<out Component>>

    open fun dispose() { }
}
