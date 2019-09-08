package com.krishnakandula.reify.dsl

interface Builder<OUTPUT> {

    fun build(): OUTPUT
}
