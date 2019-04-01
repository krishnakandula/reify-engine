package com.krishnakandula.reify

import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.systems.System
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.TreeSet
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Engine {

    companion object {
        private const val FIXED_INTERVAL = 1f / 100f
    }

    val gameSystemsMap = HashMap<Class<out System>, System>()
    val gameSystems: MutableSet<System> = TreeSet(Comparator<System> { s1, s2 ->
        return@Comparator if (s1.priority == s2.priority) {
            1
        } else {
            s1.priority.compareTo(s2.priority)
        }
    })

    private val componentFamilies = mutableMapOf<Class<out Component>, MutableSet<GameObject>>()
    private val gameObjectsByTag = mutableMapOf<String, MutableSet<GameObject>>()
    private val gameObjectsById = mutableMapOf<String, GameObject>()
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
            gameSystems.filter(System::enabled)
                    .forEach { it.fixedUpdate(FIXED_INTERVAL, filterGameObjects(it)) }
            accumulator -= FIXED_INTERVAL
        }
        gameSystems.filter(System::enabled)
                .forEach { it.update(deltaTime, filterGameObjects(it)) }
    }

    private fun addGameObject(obj: GameObject) {
        obj.getComponents().forEach { component ->
            componentFamilies.computeIfAbsent(component.javaClass) { HashSet() }.add(obj)
        }
        gameObjectsById[obj.id] = obj
        gameObjectsByTag.computeIfAbsent(obj.tag) { HashSet() }.add(obj)
    }

    fun addGameObjects(vararg objs: GameObject) {
        objs.forEach(this::addGameObject)
    }

    fun getGameObjectById(id: String): GameObject? = gameObjectsById[id]

    fun getGameObjectsByTag(tag: String): Set<GameObject> = gameObjectsByTag.getOrDefault(tag, HashSet())

    private fun removeGameObject(obj: GameObject) {
        componentFamilies.forEach { _, gameObjectsSet -> gameObjectsSet.remove(obj) }
        gameObjectsById.remove(obj.id)
        gameObjectsByTag.getOrDefault(obj.tag, null)?.remove(obj)
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

    private fun filterGameObjects(system: System): Collection<GameObject> {
        val filters = system.getFilters()
        if (filters.isEmpty()) {
            return gameObjectsById.values
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
