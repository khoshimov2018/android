package com.example.dating.models

data class LocationModel(
    var type: String = "Feature",
    var properties: PropertiesModel = PropertiesModel(),
    var geometry: Geometry = Geometry()
)

data class PropertiesModel(
    var name: String = ""
)

data class Geometry (
    var type: String = "Point",
    var coordinates: MutableList<Double> = ArrayList()
)