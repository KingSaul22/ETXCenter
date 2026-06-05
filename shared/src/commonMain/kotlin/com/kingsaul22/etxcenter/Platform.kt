package com.kingsaul22.etxcenter

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform