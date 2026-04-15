package org.example.kotlinchattest.scalable

open class Message(
    val user: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)