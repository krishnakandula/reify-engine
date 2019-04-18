package com.krishnakandula.reify.systems.collisions

import com.badlogic.gdx.math.Rectangle
import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.components.CollisionComponent
import com.krishnakandula.reify.components.TransformComponent
import com.krishnakandula.reify.systems.System
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class CollisionSystem(private var boundingBoxWidth: Float,
                      private var boundingBoxHeight: Float,
                      private var boundingBoxX: Float = DEFAULT_X_OFFSET,
                      private var boundingBoxY: Float = DEFAULT_Y_OFFSET,
                      private val spatialHashWidth: Int = SPATIAL_HASH_WIDTH,
                      private val spatialHashHeight: Int = SPATIAL_HASH_HEIGHT) : System(120) {

    companion object {
        private const val SPATIAL_HASH_WIDTH = 6
        private const val SPATIAL_HASH_HEIGHT = 6
        private const val DEFAULT_X_OFFSET = 0f
        private const val DEFAULT_Y_OFFSET = 0f

        private val filters = listOf(CollisionComponent::class.java, TransformComponent::class.java)
    }

    private val collisionPublisher = PublishSubject.create<Collision>()
    private val collisions = mutableSetOf<Collision>()
    private var spatialHash: Array<Cell> = createSpatialHash()

    fun observeCollisions(): Observable<Collision> = collisionPublisher

    fun setBoundingBoxWidth(width: Float) {
        this.boundingBoxWidth = width
        this.spatialHash = createSpatialHash()
    }

    fun setBoundingBoxHeight(height: Float) {
        this.boundingBoxHeight = height
        this.spatialHash = createSpatialHash()
    }

    fun setBoundingBoxX(x: Float) {
        this.boundingBoxX = x
        this.spatialHash = createSpatialHash()
    }

    fun setBoundingBoxY(y: Float) {
        this.boundingBoxY = y
        this.spatialHash = createSpatialHash()
    }

    fun getBoundingBoxHeight(): Float = this.boundingBoxHeight

    fun getBoundingBoxWidth(): Float = this.boundingBoxWidth

    fun getBoundingBoxX(): Float = this.boundingBoxX

    fun getBoundingBoxY(): Float = this.boundingBoxY

    override fun fixedUpdate(deltaTime: Float, gameObjects: Collection<GameObject>) {
        super.fixedUpdate(deltaTime, gameObjects)

        collisions.clear()
        spatialHash?.forEach(Cell::clear)
        gameObjects.forEach(this::fixedUpdate)
    }

    private fun fixedUpdate(gameObject: GameObject) {
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

    private fun createSpatialHash(): Array<Cell> {
        val cellWidth = boundingBoxWidth / spatialHashWidth
        val cellHeight = boundingBoxHeight / spatialHashHeight

        val rows = spatialHashWidth
        val cols = spatialHashHeight

        return Array(rows * cols) { index ->
            val row = index / rows
            val col = index % rows

            return@Array Cell((row * cellWidth) + boundingBoxX, (col * cellHeight) + boundingBoxY, cellWidth, cellHeight)
        }
    }

    private fun checkCollision(o1: GameObject, o2: GameObject): Boolean {
        val transform1 = o1.getComponent<TransformComponent>() ?: return false
        val transform2 = o2.getComponent<TransformComponent>() ?: return false

        return transform1.getRect().overlaps(transform2.getRect())
    }

    private class Cell(x: Float,
                       y: Float,
                       val width: Float,
                       val height: Float) {

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
