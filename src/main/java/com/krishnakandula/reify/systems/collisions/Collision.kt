package com.krishnakandula.reify.systems.collisions

import com.krishnakandula.reify.GameObject

class Collision(val o1: GameObject,
                val o2: GameObject) {

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Collision) {
            return false
        }

        return ((this.o1 == other.o1 && this.o2 == other.o2)
                || (this.o1 == other.o2 && this.o2 == other.o1))
    }

    override fun hashCode(): Int = o1.hashCode() + o2.hashCode()
}
