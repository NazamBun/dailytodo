package com.nazam.dailytodo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform