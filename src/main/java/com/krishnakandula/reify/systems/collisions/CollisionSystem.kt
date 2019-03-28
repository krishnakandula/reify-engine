package com.krishnakandula.reify.systems.collisions

import com.badlogic.gdx.math.Rectangle
import com.krishnakandula.reify.Engine
import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.components.CollisionComponent
import com.krishnakandula.reify.components.TransformComponent
import com.krishnakandula.reify.systems.System
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class CollisionSystem(private val spatialHashWidth: Int = SPATIAL_HASH_WIDTH,
                      private val spatialHashHeight: Int = SPATIAL_HASH_HEIGHT) : System(120) {

    companion object {
        private const val SPATIAL_HASH_WIDTH = 6
        private const val SPATIAL_HASH_HEIGHT = 6

        private val filters = listOf(CollisionComponent::class.java, TransformComponent::class.java)
    }

    private val collisionPublisher = PublishSubject.create<Collision>()

    private var engine: Engine? = null
    private var spatialHash: Array<Cell>? = null
    private val collisions = mutableSetOf<Collision>()
    private val disposable = CompositeDisposable()

    fun observeCollisions(): Observable<Collision> = collisionPublisher

    override fun onAddedToEngine(engine: Engine) {
        this.engine = engine
        disposable.add(engine.observeScreenResize().subscribe { screenSize ->
            spatialHash = createSpatialHash(screenSize.first, screenSize.second)
        })
    }

    override fun onRemovedFromEngine() {
        disposable.clear()
        engine = null
    }

    override fun onStartFixedUpdating(deltaTime: Float) {
        super.onStartFixedUpdating(deltaTime)

        collisions.clear()
        spatialHash?.forEach(Cell::clear)
    }

    override fun fixedUpdate(deltaTime: Float, gameObject: GameObject) {
        spatialHash?.filter { cell -> cell.contains(gameObject) }
                ?.forEach { cell ->
                    val gameObjects = cell.getGameObjects()
                    gameObjects.forEach { obj ->
                        if (checkCollision(gameObject, obj)) {
                            val collision = Collision(gameObject, obj)
                            if (!collisions.contains(collision)) {
                                collisions.add(collision)
                                collisionPublisher.onNext(collision)
                            }
                        }
                    }
                    cell.addGameObject(gameObject)
                }
    }

    override fun getFilters() = filters

    private fun createSpatialHash(viewportWidth: Float, viewportHeight: Float): Array<Cell> {
        val cellWidth = viewportWidth / spatialHashWidth
        val cellHeight = viewportHeight / spatialHashHeight

        val rows = spatialHashWidth
        val cols = spatialHashHeight

        return Array(rows * cols) { index ->
            val row = index / rows
            val col = index % rows

            return@Array Cell(row * cellWidth, col * cellHeight, cellWidth, cellHeight)
        }
    }

    private fun checkCollision(o1: GameObject, o2: GameObject): Boolean {
        val transform1 = o1.getComponent<TransformComponent>() ?: return false
        val transform2 = o2.getComponent<TransformComponent>() ?: return false

        return transform1.getRect().overlaps(transform2.getRect())
    }

    private class Cell(x: Float,
                       y: Float,
                       width: Float,
                       height: Float) {

        private val gameObjects = mutableListOf<GameObject>()
        private val rect = Rectangle(x, y, width, height)

        fun getGameObjects(): List<GameObject> = gameObjects

        fun clear() {
            gameObjects.clear()
        }

        /**
         * Returns if a part of a GameObject is located within the cell
         */
        fun contains(obj: GameObject): Boolean {
            val objTransform = obj.getComponent<TransformComponent>() ?: return false

            return rect.overlaps(objTransform.getRect())
        }

        fun addGameObject(obj: GameObject) {
            gameObjects.add(obj)
        }
    }
}
