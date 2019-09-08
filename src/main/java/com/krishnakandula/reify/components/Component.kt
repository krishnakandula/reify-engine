package com.krishnakandula.reify.components

import com.krishnakandula.reify.dsl.ReifyDSL

@ReifyDSL
interface Component {
    fun dispose() { }
}
