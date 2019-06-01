package com.krishnakandula.reify

import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Intersector

fun Rectangle.toPolygon(): Polygon {
    return Polygon(floatArrayOf(0f, 0f, width, 0f, width, height, 0f, height)).apply {
        setPosition(x,y)
    }
}

fun Rectangle.overlaps(x2: Float, y2: Float, width2: Float, height2: Float): Boolean {
    return x < x2 + width2 && x + width > x2 && y < y2 + height2 && y + height > y2
}

fun Polygon.overlaps(circle: Circle): Boolean {
    val vertices = transformedVertices
    val center = Vector2(circle.x, circle.y)
    val squareRadius = circle.radius * circle.radius
    var i = 0
    while (i < vertices.size) {
        if (i == 0) {
            if (Intersector.intersectSegmentCircle(Vector2(vertices[vertices.size - 2], vertices[vertices.size - 1]), Vector2(vertices[i], vertices[i + 1]), center, squareRadius))
                return true
        } else if (Intersector.intersectSegmentCircle(Vector2(vertices[i - 2], vertices[i - 1]), Vector2(vertices[i], vertices[i + 1]), center, squareRadius)) {
            return true
        }
        i += 2
    }
    return contains(circle.x, circle.y)
}
