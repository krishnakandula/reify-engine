package com.krishnakandula.reify.systems.collisions

import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Shape2D
import com.badlogic.gdx.math.Vector2
import com.krishnakandula.reify.GameObject
import com.krishnakandula.reify.components.HitboxComponent
import com.krishnakandula.reify.components.TransformComponent
import com.krishnakandula.reify.overlaps
import com.krishnakandula.reify.systems.System
import com.krishnakandula.reify.toPolygon
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class CollisionSystem(private var boundingBoxWidth: Float,
                      private var boundingBoxHeight: Float,
                      private val continuous: Boolean = true,
                      private var boundingBoxX: Float = DEFAULT_X_OFFSET,
                      private var boundingBoxY: Float = DEFAULT_Y_OFFSET,
                      private val spatialHashWidth: Int = SPATIAL_HASH_WIDTH,
                      private val spatialHashHeight: Int = SPATIAL_HASH_HEIGHT) : System(120) {

    companion object {
        private const val SPATIAL_HASH_WIDTH = 20
        private const val SPATIAL_HASH_HEIGHT = 20
        private const val DEFAULT_X_OFFSET = 0f
        private const val DEFAULT_Y_OFFSET = 0f

        private val filters = listOf(HitboxComponent::class.java, TransformComponent::class.java)
    }

    private val collisionPublisher = PublishSubject.create<Collision>()
    private val currentFrameCollisions = mutableSetOf<Collision>()
    private val previousFrameCollisions = mutableSetOf<Collision>()
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

    fun checkCollision(o1: GameObject, o2: GameObject): Boolean {
        val hitBox1 = scene?.getComponent<HitboxComponent>(o1) ?: return false
        val hitBox2 = scene?.getComponent<HitboxComponent>(o2) ?: return false

        return hitBox1.shape.collides(hitBox2.shape)
    }

    override fun fixedUpdate(deltaTime: Float, gameObjects: Collection<GameObject>) {
        super.fixedUpdate(deltaTime, gameObjects)

        spatialHash.forEach(Cell::clear)
        gameObjects.forEach(this::fixedUpdate)
        if (!continuous) {
            previousFrameCollisions.clear()
            previousFrameCollisions.addAll(currentFrameCollisions)
        }
        currentFrameCollisions.clear()
    }

    private fun fixedUpdate(gameObject: GameObject) {
        updateHitBox(gameObject)
        spatialHash
                .filter { cell -> cell.contains(gameObject) }
                .forEach { cell ->
                    val gameObjects = cell.getGameObjects()
                    gameObjects.forEach { obj ->
                        if (checkCollision(gameObject, obj)) {
                            val collision = Collision(gameObject, obj)
                            if (!currentFrameCollisions.contains(collision)) {
                                currentFrameCollisions.add(collision)
                                if (continuous || (!previousFrameCollisions.contains(collision))) {
                                    collisionPublisher.onNext(collision)
                                }
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

    private fun updateHitBox(gameObject: GameObject) {
        val transform = scene?.getComponent<TransformComponent>(gameObject) ?: return
        val hitBox = scene?.getComponent<HitboxComponent>(gameObject) ?: return

        when (val shape = hitBox.shape) {
            is Rectangle -> {
                shape.setCenter(
                        transform.position.x + (transform.width / 2) + hitBox.offsetX,
                        transform.position.y + (transform.height / 2) + hitBox.offsetY)
            }
            is Polygon -> {
                shape.setPosition(
                        transform.position.x + (transform.width / 2) + hitBox.offsetX,
                        transform.position.y + (transform.height / 2) + hitBox.offsetY)
            }
            is Circle -> {
                shape.setPosition(
                        transform.position.x + (transform.width / 2) + hitBox.offsetX,
                        transform.position.y + (transform.height / 2) + hitBox.offsetY)
            }
        }
    }

    private fun Shape2D.collides(other: Shape2D): Boolean {
        return when (this) {
            is Rectangle -> this.collides(other)
            is Circle -> this.collides(other)
            is Polygon -> this.collides(other)
            else -> false
        }
    }

    private fun Polygon.collides(other: Shape2D): Boolean {
        return when (other) {
            is Circle -> overlaps(this, other)
            is Rectangle -> other.collides(other)
            is Polygon -> Intersector.overlapConvexPolygons(this, other)
            else -> false
        }
    }

    private fun Circle.collides(other: Shape2D): Boolean {
        return when (other) {
            is Circle -> this.overlaps(other)
            is Rectangle -> other.collides(this)
            is Polygon -> overlaps(other, this)
            else -> false
        }
    }

    private fun Rectangle.collides(other: Shape2D): Boolean {
        return when (other) {
            is Rectangle -> this.overlaps(other)
            is Polygon -> Intersector.overlapConvexPolygons(this.toPolygon(), other)
            is Circle -> Intersector.overlaps(other, this)
            else -> false
        }
    }

    private fun overlaps(polygon: Polygon, circle: Circle): Boolean {
        val vertices = polygon.transformedVertices
        val center = Vector2(circle.x, circle.y)
        val squareRadius = circle.radius * circle.radius
        var i = 0
        while (i < vertices.size) {
            if (i == 0) {
                if (Intersector.intersectSegmentCircle(Vector2(vertices[vertices.size - 2], vertices[vertices.size - 1]), Vector2(vertices[i], vertices[i + 1]), center, squareRadius))
                    return true
            } else {
                if (Intersector.intersectSegmentCircle(Vector2(vertices[i - 2], vertices[i - 1]), Vector2(vertices[i], vertices[i + 1]), center, squareRadius))
                    return true
            }
            i += 2
        }
        return polygon.contains(circle.x, circle.y)
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
         * Returns if a part of the GameObject's hit box is located within the cell
         */
        fun contains(obj: GameObject): Boolean {
            val objHitBox = scene?.getComponent<HitboxComponent>(obj) ?: return false

            return when (val objShape = objHitBox.shape) {
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
