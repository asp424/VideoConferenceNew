package com.example.videoconference.models

data class UserModel(
    var user_id: String = "",
    var first_name: String = "",
    var last_name: String = "",
    var avatar: String = "",
    var email: String = "",
    var token: String = "",
    var password: String = "",
    var checked: Boolean = false,
    var chatId: String = ""
)