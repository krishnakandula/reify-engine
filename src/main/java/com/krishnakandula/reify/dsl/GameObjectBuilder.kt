package com.krishnakandula.reify.dsl

import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.Scene
import com.krishnakandula.reify.components.Component

class GameObjectBuilder(private val scene: Scene) : Builder<GameObject> {

    var tag = ""
    val components = mutableListOf<Component>()

    override fun build(): GameObject {
        val gameObject = scene.createGameObject(tag)
        components.forEach { component ->
            scene.addComponent(component::class.java, component, gameObject)
            return@forEach
        }

        return gameObject
    }

    operator fun <T : Component> T.unaryPlus() {
        components.add(this)
    }
}

fun Scene.gameObject(init: GameObjectBuilder.() -> Unit): GameObject {
    val gameObjectBuilder = GameObjectBuilder(this)
    gameObjectBuilder.init()

    return gameObjectBuilder.build()
}
