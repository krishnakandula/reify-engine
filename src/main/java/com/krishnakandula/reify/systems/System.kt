package com.krishnakandula.reify.systems

import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.Scene

abstract class System(val priority: Short = 1,
                      var enabled: Boolean = true) {

    var scene: Scene? = null

    abstract fun getFilters(): List<Class<out Component>>

    open fun onAddedToScene(scene: Scene) { }

    open fun onRemovedFromScene(scene: Scene) { }

    open fun update(deltaTime: Float, gameObjects: Collection<GameObject>) { }

    open fun fixedUpdate(deltaTime: Float, gameObjects: Collection<GameObject>) { }

    open fun resize(width: Float, height: Float) { }

    open fun dispose() { }
}
