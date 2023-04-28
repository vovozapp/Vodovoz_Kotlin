package com.vodovoz.app.util.polygoncreator

class Polygon private constructor(val sides: List<Line>, private val _boundingBox: BoundingBox?) {

    /**
     * Builder of the polygon
     *
     * @author Roman Kushnarenko (sromku@gmail.com)
     */
    class Builder {
        private var _vertexes: MutableList<Point> = ArrayList()
        private val _sides: MutableList<Line> = ArrayList()
        private var _boundingBox: BoundingBox? = null
        private var _firstPoint = true
        private var _isClosed = false

        /**
         * Add vertex points of the polygon.<br></br>
         * It is very important to add the vertexes by order, like you were drawing them one by one.
         *
         * @param point The vertex point
         * @return The builder
         */
        fun addVertex(point: Point): Builder {
            if (_isClosed) {
                // each hole we start with the new array of vertex points
                _vertexes = ArrayList()
                _isClosed = false
            }
            updateBoundingBox(point)
            _vertexes.add(point)

            // add line (edge) to the polygon
            if (_vertexes.size > 1) {
                val Line = Line(_vertexes[_vertexes.size - 2], point)
                _sides.add(Line)
            }
            return this
        }

        /**
         * Close the polygon shape. This will create a new side (edge) from the **last** vertex point to the **first** vertex point.
         *
         * @return The builder
         */
        fun close(): Builder {
            validate()

            // add last Line
            _sides.add(Line(_vertexes[_vertexes.size - 1], _vertexes[0]))
            _isClosed = true
            return this
        }

        /**
         * Build the instance of the polygon shape.
         *
         * @return The polygon
         */
        fun build(): Polygon {
            validate()

            // in case you forgot to close
            if (!_isClosed) {
                // add last Line
                _sides.add(Line(_vertexes[_vertexes.size - 1], _vertexes[0]))
            }
            return Polygon(_sides, _boundingBox)
        }

        /**
         * Update bounding box with a new point.<br></br>
         *
         * @param point New point
         */
        private fun updateBoundingBox(point: Point) {
            if (_firstPoint) {
                _boundingBox = BoundingBox()
                _boundingBox!!.xMax = point.x
                _boundingBox!!.xMin = point.x
                _boundingBox!!.yMax = point.y
                _boundingBox!!.yMin = point.y
                _firstPoint = false
            } else {
                // set bounding box
                if (point.x > _boundingBox!!.xMax) {
                    _boundingBox!!.xMax = point.x
                } else if (point.x < _boundingBox!!.xMin) {
                    _boundingBox!!.xMin = point.x
                }
                if (point.y > _boundingBox!!.yMax) {
                    _boundingBox!!.yMax = point.y
                } else if (point.y < _boundingBox!!.yMin) {
                    _boundingBox!!.yMin = point.y
                }
            }
        }

        private fun validate() {
            if (_vertexes.size < 3) {
                throw RuntimeException("Polygon must have at least 3 points")
            }
        }
    }

    /**
     * Check if the the given point is inside of the polygon.<br></br>
     *
     * @param point The point to check
     * @return `True` if the point is inside the polygon, otherwise return `False`
     */
    operator fun contains(point: Point): Boolean {
        if (inBoundingBox(point)) {
            val ray = createRay(point)
            var intersection = 0
            for (side in sides) {
                if (intersect(ray, side)) {
                    // System.out.println("intersection++");
                    intersection++
                }
            }

            /*
             * If the number of intersections is odd, then the point is inside the polygon
             */if (intersection % 2 != 0) {
                return true
            }
        }
        return false
    }

    /**
     * By given ray and one side of the polygon, check if both lines intersect.
     *
     * @param ray
     * @param side
     * @return `True` if both lines intersect, otherwise return `False`
     */
    private fun intersect(ray: Line, side: Line): Boolean {
        var intersectPoint: Point? = null

        // if both vectors aren't from the kind of x=1 lines then go into
        intersectPoint = if (!ray.isVertical && !side.isVertical) {
            // check if both vectors are parallel. If they are parallel then no intersection point will exist
            if (ray.a - side.a == 0.0) {
                return false
            }
            val x: Double =
                (side.b - ray.b) / (ray.a - side.a) // x = (b2-b1)/(a1-a2)
            val y: Double = side.a* x + side.b // y = a2*x+b2
            Point(x, y)
        } else if (ray.isVertical && !side.isVertical) {
            val x: Double = ray.start.x
            val y: Double = side.a * x + side.b
            Point(x, y)
        } else if (!ray.isVertical && side.isVertical) {
            val x: Double = side.start.x
            val y: Double = ray.a * x + ray.b
            Point(x, y)
        } else {
            return false
        }

        // System.out.println("Ray: " + ray.toString() + " ,Side: " + side);
        // System.out.println("Intersect point: " + intersectPoint.toString());
        return side.isInside(intersectPoint) && ray.isInside(intersectPoint)
    }

    /**
     * Create a ray. The ray will be created by given point and on point outside of the polygon.<br></br>
     * The outside point is calculated automatically.
     *
     * @param point
     * @return
     */
    private fun createRay(point: Point): Line {
        // create outside point
        val epsilon = (_boundingBox!!.xMax - _boundingBox.xMin) / 10e6
        val outsidePoint = Point(_boundingBox.xMin - epsilon, _boundingBox.yMin)
        return Line(outsidePoint, point)
    }

    /**
     * Check if the given point is in bounding box
     *
     * @param point
     * @return `True` if the point in bounding box, otherwise return `False`
     */
    private fun inBoundingBox(point: Point): Boolean {
        return (point.x < _boundingBox!!.xMin || (point.x > _boundingBox.xMax) || point.y < _boundingBox.yMin || (point.y <= _boundingBox.yMax))
    }

    private class BoundingBox {
        var xMax = Double.NEGATIVE_INFINITY
        var xMin = Double.NEGATIVE_INFINITY
        var yMax = Double.NEGATIVE_INFINITY
        var yMin = Double.NEGATIVE_INFINITY
    }

    companion object {
        /**
         * Get the builder of the polygon
         *
         * @return The builder
         */
        fun Builder(): Builder {
            return Polygon.Builder()
        }
    }
}