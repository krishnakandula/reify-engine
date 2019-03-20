package com.krishnakandula.reify

import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.systems.System
import java.util.*

class Engine {

    private val gameSystems: MutableSet<System> = TreeSet(Comparator<System> { s1, s2 ->
        return@Comparator if (s1.priority == s2.priority) {
            1
        } else {
            s1.priority.compareTo(s2.priority)
        }
    })

    private val componentFamilies: MutableMap<Class<out Component>, MutableSet<GameObject>> = mutableMapOf()
    private val gameObjects: MutableSet<GameObject> = mutableSetOf()

    fun addSystems(vararg systems: System) {
        gameSystems.addAll(systems)
    }

    fun removeSystem(system: System) {
        gameSystems.remove(system)
    }

    fun update(deltaTime: Float) {
        gameSystems.filter { it.enabled }
                .forEach { system ->
                    filterGameObjects(system)
                            .forEach { gameObject ->
                                system.process(deltaTime, gameObject)
                            }
                }
    }

    fun addGameObject(obj: GameObject) {
        obj.getComponents().forEach { component ->
            componentFamilies.computeIfAbsent(component.javaClass) { HashSet() }.add(obj)
        }
        gameObjects.add(obj)
    }

    fun addGameObjects(vararg objs: GameObject) {
        objs.forEach(this::addGameObject)
    }

    fun removeGameObject(obj: GameObject) {
        componentFamilies.forEach { _, gameObjectsSet -> gameObjectsSet.remove(obj) }
        gameObjects.remove(obj)
    }

    fun removeGameObjects(vararg objs: GameObject) {
        objs.forEach(this::removeGameObject)
    }

    fun dispose() {
        gameSystems.forEach(System::dispose)
        gameSystems.clear()
    }

    private fun filterGameObjects(system: System): Set<GameObject> {
        val filters = system.getFilters()
        if (filters.isEmpty()) {
            return gameObjects
        }
        val objs = componentFamilies.getOrDefault(filters.first(), mutableSetOf())
        return objs.filter { obj ->
            filters.forEach { filter ->
                if (!componentFamilies.getOrDefault(filter, mutableSetOf()).contains(obj)) {
                    return@filter false
                }
            }
            return@filter true
        }.toSet()
    }
}
