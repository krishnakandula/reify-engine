package com.krishnakandula.reify

import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.systems.System
import java.util.TreeSet

abstract class Scene {

    protected val gameSystemsMap = HashMap<Class<out System>, System>()
    protected val gameSystems = TreeSet(Comparator<System> { s1, s2 ->
        return@Comparator if (s1.priority == s2.priority) {
            1
        } else {
            s1.priority.compareTo(s2.priority)
        }
    })

    protected val componentFamilies = mutableMapOf<Class<out Component>, MutableSet<GameObject>>()
    private val gameObjectsByTag = mutableMapOf<String, MutableSet<GameObject>>()
    private val gameObjectsById = mutableMapOf<String, GameObject>()

    open fun update(deltaTime: Float) {
        gameSystems.filter(System::enabled)
                .forEach { it.update(deltaTime, filterGameObjects(it)) }
    }

    open fun fixedUpdate(deltaTime: Float) {
        gameSystems.filter(System::enabled)
                .forEach { it.fixedUpdate(deltaTime, filterGameObjects(it)) }
    }

    open fun resize(width: Float, height: Float) {
        gameSystems.forEach { it.resize(width, height) }
    }

    fun addGameObjects(vararg objs: GameObject) {
        objs.forEach(this::addGameObject)
    }

    fun removeGameObjects(vararg objs: GameObject) {
        objs.forEach(this::removeGameObject)
    }

    inline fun <reified T : Component> addComponent(component: T, gameObject: GameObject): Boolean {
        if (gameObject.hasComponent<T>()) {
            return false
        }

        removeComponent<T>(gameObject)
        gameObject.addComponent(component)
        componentFamilies.computeIfAbsent(T::class.java) { HashSet() }.add(gameObject)

        return true
    }

    inline fun <reified T : Component> removeComponent(gameObject: GameObject): Boolean {
        if (!gameObject.hasComponent<T>()) {
            return false
        }

        gameObject.removeComponent<T>()
        componentFamilies[T::class.java]?.remove(gameObject)

        return true
    }

    fun getGameObjectById(id: String): GameObject? = gameObjectsById[id]

    fun getGameObjectsByTag(tag: String): Set<GameObject> = gameObjectsByTag.getOrDefault(tag, HashSet())

    open fun dispose() {
        gameSystems.forEach(System::dispose)
        gameSystems.clear()
        gameObjectsById.values.forEach(GameObject::dispose)
    }

    inline fun <reified T : System> addSystem(system: T) {
        if (gameSystems.contains(system) || gameSystemsMap.containsKey(T::class.java)) {
            return
        }
        gameSystems.add(system)
        gameSystemsMap[T::class.java] = system
        system.onAddedToScene(this)
    }

    inline fun <reified T : System> removeSystem() {
        val system = gameSystemsMap.remove(T::class.java)
        if (system != null) {
            gameSystems.remove(system)
            system.onRemovedFromScene()
        }
    }

    inline fun <reified T : System> getSystem(): T? {
        return if (gameSystemsMap.containsKey(T::class.java)) {
            gameSystemsMap[T::class.java] as T
        } else null
    }

    private fun addGameObject(obj: GameObject) {
        obj.getComponents().forEach { component ->
            addComponent(component, obj)
        }
        gameObjectsById[obj.id] = obj
        gameObjectsByTag.computeIfAbsent(obj.tag) { HashSet() }.add(obj)
    }

    private fun removeGameObject(obj: GameObject) {
        componentFamilies.forEach { (_, gameObjectsSet) -> gameObjectsSet.remove(obj) }
        gameObjectsById.remove(obj.id)
        gameObjectsByTag.getOrDefault(obj.tag, null)?.remove(obj)
    }

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
