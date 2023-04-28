package com.vodovoz.app.util.polygoncreator

class Line(
    /**
     * Get start point
     *
     * @return The start point
     */
    val start: Point,
    /**
     * Get end point
     *
     * @return The end point
     */
    var end: Point
) {

    /**
     * y = **A**x + B
     *
     * @return The **A**
     */
    var a = Double.NaN

    /**
     * y = Ax + **B**
     *
     * @return The **B**
     */
    var b = Double.NaN

    /**
     * Indicate whereas the line is vertical. <br></br>
     * For example, line like x=1 is vertical, in other words parallel to axis Y. <br></br>
     * In this case the A is (+/-)infinite.
     *
     * @return `True` if the line is vertical, otherwise return `False`
     */
    var isVertical = false

    init {
        if (end.x - start.x != 0.0) {
            a = (end.y - start.y) / (end.x - start.x)
            b = start.y - a * start.x
        } else {
            isVertical = true
        }
    }

    /**
     * Indicate whereas the point lays on the line.
     *
     * @param point - The point to check
     * @return `True` if the point lays on the line, otherwise return `False`
     */
    fun isInside(point: Point): Boolean {
        val maxX = if (start.x > end.x) start.x else end.x
        val minX = if (start.x < end.x) start.x else end.x
        val maxY = if (start.y > end.y) start.y else end.y
        val minY = if (start.y < end.y) start.y else end.y
        return point.x in minX..maxX && point.y >= minY && point.y <= maxY
    }

    override fun toString(): String {
        return java.lang.String.format("%s-%s", start.toString(), end.toString())
    }
}