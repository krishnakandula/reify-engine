package com.krishnakandula.reify

import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.systems.System
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import kotlin.collections.HashMap

class Engine {

    companion object {
        private const val FIXED_INTERVAL = 1f / 100f
    }

    protected val gameSystemsMap = HashMap<Class<out System>, System>()
    protected val gameSystems: MutableSet<System> = TreeSet(Comparator<System> { s1, s2 ->
        return@Comparator if (s1.priority == s2.priority) {
            1
        } else {
            s1.priority.compareTo(s2.priority)
        }
    })

    private val componentFamilies = mutableMapOf<Class<out Component>, MutableSet<GameObject>>()
    private val gameObjects = mutableSetOf<GameObject>()
    private val resizeSubject = BehaviorSubject.create<Pair<Float, Float>>()

    private var accumulator = 0f

    inline fun <reified T : System> addSystem(system: T) {
        if (gameSystems.contains(system) || gameSystemsMap.containsKey(T::class.java)) {
            return
        }
        gameSystems.add(system)
        gameSystemsMap[T::class.java] = system
        system.onAddedToEngine(this)
    }

    inline fun <reified T : System> removeSystem() {
        val system = gameSystemsMap.remove(T::class.java)
        if (system != null) {
            gameSystems.remove(system)
            system.onRemovedFromEngine()
        }
    }

    inline fun <reified T : System> getSystem(): T? {
        return if (gameSystemsMap.containsKey(T::class.java)) {
            gameSystemsMap[T::class.java] as T
        } else null
    }

    fun update(deltaTime: Float) {
        accumulator += deltaTime
        while (accumulator >= FIXED_INTERVAL) {
            gameSystems.forEach { it.onStartFixedUpdating(FIXED_INTERVAL) }
            gameSystems.filter(System::enabled)
                    .forEach { system ->
                        filterGameObjects(system)
                                .forEach { gameObject ->
                                    system.fixedUpdate(FIXED_INTERVAL, gameObject)
                                }
                    }
            accumulator -= FIXED_INTERVAL
        }
        gameSystems.forEach { it.onStartUpdating(deltaTime, this) }
        gameSystems.filter(System::enabled)
                .forEach { system ->
                    filterGameObjects(system)
                            .forEach { gameObject ->
                                system.update(deltaTime, gameObject)
                            }
                }
    }

    private fun addGameObject(obj: GameObject) {
        obj.getComponents().forEach { component ->
            componentFamilies.computeIfAbsent(component.javaClass) { HashSet() }.add(obj)
        }
        gameObjects.add(obj)
    }

    fun addGameObjects(vararg objs: GameObject) {
        objs.forEach(this::addGameObject)
    }

    private fun removeGameObject(obj: GameObject) {
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
