package com.rogerio.geocomms.model

data class ChatMessage(
    val id: String,
    val sender: Sender,
    val text: String? = null,
    val audioPath: String? = null, // local file path
    val timestamp: Long = System.currentTimeMillis()
)

enum class Sender {
    USER, EXTERNAL, SYSTEM
}
