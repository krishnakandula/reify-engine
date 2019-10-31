package com.krishnakandula.reify

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.krishnakandula.reify.components.Component
import com.krishnakandula.reify.systems.System
import java.util.TreeSet

abstract class Scene(protected val spriteBatch: SpriteBatch,
                     protected val shapeRenderer: ShapeRenderer) {

    protected val gameSystemsMap = HashMap<Class<out System>, System>()
    protected val gameSystems = TreeSet(Comparator<System> { s1, s2 ->
        return@Comparator if (s1.priority == s2.priority) {
            1
        } else {
            s1.priority.compareTo(s2.priority)
        }
    })

    private val componentFamilies = mutableMapOf<Class<out Component>, MutableSet<GameObject>>()
    private val gameObjectsByTag = mutableMapOf<String, MutableSet<GameObject>>()
    private val gameObjectsById = mutableMapOf<String, GameObject>()
    private val componentMap = mutableMapOf<String, MutableMap<Class<out Component>, Component>>()

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

    fun removeGameObjects(vararg objs: GameObject) {
        objs.forEach(this::removeGameObject)
    }

    fun hasGameObject(gameObject: GameObject): Boolean {
        return gameObjectsById.containsKey(gameObject.id)
    }

    inline fun <reified T : Component> hasComponent(gameObject: GameObject): Boolean {
        return hasComponent(T::class.java, gameObject)
    }

    fun <T : Component> hasComponent(componentClazz: Class<T>, gameObject: GameObject): Boolean {
        if (!hasGameObject(gameObject)) {
            return false
        }

        return getComponents(gameObject).containsKey(componentClazz)
    }

    fun getComponents(gameObject: GameObject): Map<Class<out Component>, Component> {
        return componentMap.getOrDefault(gameObject.id, emptyMap())
    }

    inline fun <reified T : Component> getComponent(gameObject: GameObject): T? {
        if (!hasGameObject(gameObject) || !hasComponent<T>(gameObject)) {
            return null
        }
        return getComponents(gameObject)[T::class.java] as T
    }

    inline fun <reified T : Component> addComponent(component: T, gameObject: GameObject): Boolean {
        return addComponent(T::class.java, component, gameObject)
    }

    fun <T : Component> addComponent(componentClazz: Class<out T>, component: T, gameObject: GameObject): Boolean {
        if (!hasGameObject(gameObject) || hasComponent(componentClazz, gameObject)) {
            return false
        }

        componentFamilies.computeIfAbsent(componentClazz) { mutableSetOf() }.add(gameObject)
        componentMap.computeIfAbsent(gameObject.id) { mutableMapOf() }[componentClazz] = component

        return true
    }

    inline fun <reified T : Component> removeComponent(gameObject: GameObject): Boolean {
        return removeComponent(T::class.java, gameObject)
    }

    fun <T : Component> removeComponent(componentClazz: Class<T>, gameObject: GameObject): Boolean {
        if (!hasComponent(componentClazz, gameObject)) {
            return false
        }

        componentFamilies[componentClazz]?.remove(gameObject)
        componentMap[gameObject.id]?.remove(componentClazz)

        return false
    }

    fun getGameObjectById(id: String): GameObject? = gameObjectsById[id]

    fun getGameObjectsByTag(tag: String): Set<GameObject> = gameObjectsByTag.getOrDefault(tag, HashSet())

    open fun dispose() {
        gameSystems.forEach(System::dispose)
        gameSystems.clear()
    }

    fun <T : System> hasSystem(systemType: Class<T>): Boolean {
        return gameSystemsMap.containsKey(systemType)
    }

    inline fun <reified T : System> hasSystem(): Boolean {
        return hasSystem(T::class.java)
    }

    inline fun <reified T : System> addSystem(system: T) {
        addSystem(system, T::class.java)
    }

    fun <T : System> addSystem(system: T, clazz: Class<T>) {
        if (gameSystems.contains(system) || gameSystemsMap.containsKey(clazz)) {
            return
        }
        gameSystems.add(system)
        gameSystemsMap[clazz] = system
        system.onAddedToScene(this)
    }

    inline fun <reified T : System> removeSystem() {
        removeSystem(T::class.java)
    }

    fun <T : System> removeSystem(clazz: Class<T>) {
        val system = gameSystemsMap.remove(clazz)
        if (system != null) {
            gameSystems.remove(system)
            system.onRemovedFromScene()
        }
    }

    inline fun <reified T : System> getSystem(): T? {
        return getSystem(T::class.java)
    }

    fun <T : System> getSystem(clazz: Class<T>): T? {
        return if (gameSystemsMap.containsKey(clazz)) {
            gameSystemsMap[clazz] as T
        } else null
    }

    fun createGameObject(tag: String = ""): GameObject {
        val gameObject = GameObject(tag)
        addGameObject(gameObject)

        return gameObject
    }

    private fun addGameObject(obj: GameObject) {
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
        return objs.asSequence()
                .filter { obj ->
                    filters.forEach { filter ->
                        if (!componentFamilies.getOrDefault(filter, mutableSetOf()).contains(obj)) {
                            return@filter false
                        }
                    }
                    return@filter true
                }.toSet()
    }
}
