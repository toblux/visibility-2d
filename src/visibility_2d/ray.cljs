(ns visibility-2d.ray
  (:require [visibility-2d.vektor :as vektor]))

(defrecord Ray [origin direction])

(defn ray? [ray]
  (instance? Ray ray))

(defn ray
  "Creates a ray from vector u to vector v, optionally rotated by theta"
  ([u v] (ray u v 0))
  ([u v theta]
   {:pre  [(vektor/point? u) (vektor/point? v) (number? theta)]
    :post [(ray? %)]}
   (Ray. u (vektor/rotate2d (vektor/subtract2d v u) theta))))

(defn point2d
  "Returns a vector on a ray at scale t"
  [ray t]
  {:pre  [(ray? ray) (number? t)]
   :post [(vektor/point? %)]}
  (let [{:keys [origin direction]} ray]
    (vektor/point (+ (:x origin) (* t (:x direction)))
                  (+ (:y origin) (* t (:y direction))))))

(defn angle2d
  "Returns the angle between the positive x-axis and a ray's direction vector"
  [ray]
  {:pre  [(ray? ray)]
   :post [(number? %)]}
  (vektor/angle2d (:direction ray)))
