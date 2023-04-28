package com.vodovoz.app.util.polygoncreator

class Point(var x: Double, var y: Double) {
    override fun toString(): String {
        return String.format("(%f,%f)", x, y)
    }
}