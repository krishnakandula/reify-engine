package com.krishnakandula.reify.dsl.systems

import com.krishnakandula.reify.dsl.Builder
import com.krishnakandula.reify.systems.System

abstract class SystemBuilder<T : System> : Builder<T> {
    protected var priority : Short = 1
    protected var enabled = true
}
