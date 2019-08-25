package com.krishnakandula.reify

import java.util.UUID

data class GameObject(val tag: String = "", val id: String = UUID.randomUUID().toString())
