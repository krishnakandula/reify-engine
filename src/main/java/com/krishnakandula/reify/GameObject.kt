package com.krishnakandula.reify

import com.krishnakandula.reify.dsl.ReifyDSL
import java.util.UUID

@ReifyDSL
data class GameObject(val tag: String = "", val id: String = UUID.randomUUID().toString())
