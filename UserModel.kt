package com.dicoding.appstory.data.prefence

data class UserModel(
    val name: String,
    val token: String,
    val email: String,
    val isLogin: Boolean = false
)