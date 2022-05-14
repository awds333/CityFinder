package com.fedchanka.cityfinder.model.domain

data class Position(val lat: Double, val lon: Double) {
    override fun toString(): String = "$lat,$lon"
}