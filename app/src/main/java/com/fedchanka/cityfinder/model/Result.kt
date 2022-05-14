package com.fedchanka.cityfinder.model

sealed class Result<out T : Any?>

class Success<T : Any?>(val value: T) : Result<T>()
class Error(val message: String, val cause: Exception? = null) : Result<Nothing>()

fun <T : Any?> T.asSuccess(): Success<T> = Success(this)

