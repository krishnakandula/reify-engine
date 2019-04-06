package com.krishnakandula.reify.components

import box2dLight.Light

data class LightingComponent(val light: Light,
                             var enabled: Boolean) : Component
