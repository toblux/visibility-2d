(ns visibility-2d.vektor)

(defrecord Point [x y])
(def point ->Point)

(defn point? [p]
  (instance? Point p))

(defn subtract2d [u v]
  {:pre  [(point? u) (point? v)]
   :post [(point? %)]}
  (Point. (- (:x u) (:x v))
          (- (:y u) (:y v))))

(defn dot2 [u v]
  {:pre  [(point? u) (point? v)]
   :post [(number? %)]}
  (+ (* (:x u) (:x v))
     (* (:y u) (:y v))))

(defn det2d [u v]
  {:pre  [(point? u) (point? v)]
   :post [(number? %)]}
  (- (* (:x u) (:y v))
     (* (:y u) (:x v))))

(defn length2d [v]
  {:pre  [(point? v)]
   :post [(number? %)]}
  (Math/sqrt (dot2 v v)))

(defn scale2d [v s]
  {:pre  [(point? v) (number? s)]
   :post [(point? %)]}
  (Point. (* s (:x v))
          (* s (:y v))))

(defn rotate2d [v theta]
  {:pre  [(point? v) (number? theta)]
   :post [(point? %)]}
  (let [{:keys [x y]} v
        cos (Math/cos theta)
        sin (Math/sin theta)]
    (Point. (- (* x cos) (* y sin))
            (+ (* x sin) (* y cos)))))

(defn normalize2d [v]
  {:pre  [(point? v)]
   :post [(point? %)]}
  (let [length (length2d v)]
    (if (> length 0)
      (scale2d v (/ 1 length))
      v)))

(defn angle2d
  "Returns the angle between the positive x-axis and a vector"
  [v]
  {:pre  [(point? v)]
   :post [(number? %)]}
  (Math/atan2 (:y v) (:x v)))

(defn shift-vector
  "Shifts a vectors' elements like this: [a b c] -> [b c a]"
  [v]
  {:pre  [(vector? v)]
   :post [(vector? %)]}
  (if (> (count v) 1)
    (conj (vec (rest v)) (first v))
    v))
