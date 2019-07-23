package com.krishnakandula.reify.systems.collisions

import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Ellipse
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.Scene
import com.krishnakandula.reify.components.HitboxComponent
import com.krishnakandula.reify.components.TransformComponent
import com.krishnakandula.reify.overlaps
import com.krishnakandula.reify.systems.System
import com.krishnakandula.reify.toPolygon
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

        private val filters = listOf(HitboxComponent::class.java, TransformComponent::class.java)
    }

    private val collisionPublisher = PublishSubject.create<Collision>()
    private val collisions = mutableSetOf<Collision>()
    private var spatialHash: Array<Cell> = createSpatialHash()
    private var scene: Scene? = null

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

    override fun onAddedToScene(scene: Scene) {
        super.onAddedToScene(scene)
        this.scene = scene
    }

    override fun onRemovedFromScene() {
        super.onRemovedFromScene()
        this.scene = null
    }

    override fun fixedUpdate(deltaTime: Float, gameObjects: Collection<GameObject>) {
        super.fixedUpdate(deltaTime, gameObjects)

        collisions.clear()
        spatialHash?.forEach(Cell::clear)
        gameObjects.forEach(this::fixedUpdate)
    }

    private fun fixedUpdate(gameObject: GameObject) {
        updateHitbox(gameObject)
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
        val transform1 = scene?.getComponent<TransformComponent>(o1) ?: return false
        val transform2 = scene?.getComponent<TransformComponent>(o2) ?: return false

        return transform1.getRect().overlaps(transform2.getRect())
    }

    private fun updateHitbox(gameObject: GameObject) {
        val transform = scene?.getComponent<TransformComponent>(gameObject) ?: return
        val hitbox = scene?.getComponent<HitboxComponent>(gameObject) ?: return
        val shape = hitbox.shape

        when(shape) {
            is Rectangle -> {
                shape.setCenter(
                        transform.position.x + hitbox.offsetX,
                        transform.position.y + hitbox.offsetY)
            }
            is Polygon -> {
                shape.setPosition(
                        transform.position.x + hitbox.offsetX,
                        transform.position.y + hitbox.offsetY)
            }
            is Ellipse -> {
                shape.setPosition(
                        transform.position.x + hitbox.offsetX,
                        transform.position.y + hitbox.offsetY)
            }
        }
    }

    private inner class Cell(x: Float,
                       y: Float,
                       val width: Float,
                       val height: Float) {

        private val gameObjects = mutableListOf<GameObject>()
        private val rect = Rectangle(x, y, width, height)
        private val poly = rect.toPolygon()

        fun getGameObjects(): List<GameObject> = gameObjects

        fun clear() {
            gameObjects.clear()
        }

        /**
         * Returns if a part of the GameObject's hitbox is located within the cell
         */
        fun contains(obj: GameObject): Boolean {
            val objHitbox = scene?.getComponent<HitboxComponent>(obj) ?: return false
            val objShape = objHitbox.shape

            return when(objShape) {
                is Rectangle -> {
                    objShape.overlaps(rect)
                }
                is Polygon -> {
                    Intersector.overlapConvexPolygons(objShape, poly)
                }
                is Circle -> {
                    poly.overlaps(objShape)
                }
                else -> false
            }
        }

        fun addGameObject(obj: GameObject) {
            gameObjects.add(obj)
        }
    }
}
