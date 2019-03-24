package com.krishnakandula.reify

import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.systems.System
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.*

class Engine {

    private val gameSystems: MutableSet<System> = TreeSet(Comparator<System> { s1, s2 ->
        return@Comparator if (s1.priority == s2.priority) {
            1
        } else {
            s1.priority.compareTo(s2.priority)
        }
    })

    private val componentFamilies = mutableMapOf<Class<out Component>, MutableSet<GameObject>>()
    private val gameObjects = mutableSetOf<GameObject>()
    private val resizeSubject = BehaviorSubject.create<Pair<Float, Float>>()

    fun addSystems(vararg systems: System) {
        systems.forEach {
            gameSystems.add(it)
            it.onAddedToEngine(this)
        }
    }

    fun removeSystem(system: System) {
        gameSystems.remove(system)
    }

    fun update(deltaTime: Float) {
        gameSystems.forEach { it.onStartProcessing(deltaTime, this) }
        gameSystems.filter(System::enabled)
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

    fun resize(width: Float, height: Float) {
        resizeSubject.onNext(Pair(width, height))
    }

    fun dispose() {
        gameSystems.forEach(System::dispose)
        gameSystems.clear()
    }

    fun observeScreenResize(): Observable<Pair<Float, Float>> = resizeSubject

    fun getScreenSize(): Pair<Float, Float> = resizeSubject.value!!

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
