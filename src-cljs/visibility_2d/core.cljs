(ns visibility-2d.core
  (:require [goog.dom :as dom]
            [goog.events :as events]
            [goog.string :as string]
            [goog.string.format]
            [visibility-2d.ray :as ray]
            [visibility-2d.dali :as dali]
            [visibility-2d.vektor :as vektor]
            [visibility-2d.statistics :as statistics]))

(defn draw-scene! [ctx w h polygons]
  (-> ctx
      (dali/fill-style! "#333")
      (dali/fill-rect! {:x 0 :y 0 :w w :h h})
      (dali/stroke-style! "#fff")
      (dali/stroke-paths! polygons)
      (dali/fill-style! "#ddd")))

(defn intersection [ray line]
  ;; ray = p + t * r
  ;; line-ray = q + u * s
  (let [[coordinate1 coordinate2] line
        line-ray (ray/ray coordinate1 coordinate2)
        {p :origin r :direction} ray
        {q :origin s :direction} line-ray
        q-p (vektor/subtract2d q p)
        rxs (vektor/det2d r s)]
    {:t (if (= 0 rxs) 0 (/ (vektor/det2d q-p s) rxs))
     :u (if (= 0 rxs) 0 (/ (vektor/det2d q-p r) rxs))}))

(defn sort-rays [rays]
  "Sort rays based on their angles"
  (sort (comparator #(< (ray/angle2d %1) (ray/angle2d %2))) rays))

(defn all-lines [polygons]
  "Returns a list of lines (start and end points) that make up a polygon"
  (mapcat #(map list % (vektor/shift-vector %)) polygons))

(defn intersections [ray lines]
  "Returns a list of all intersections between ray and lines"
  (for [line lines
        :let [inter (intersection ray line)
              {:keys [t u]} inter]
        :when (and (> t 0) (> u 0) (<= u 1))]
    inter))

(defn all-rays [origin lines]
  (for [line lines, point line, angle [0 -0.00175 0.00175]]
    (ray/ray origin point angle)))

(defn visible-path [sorted-rays lines]
  (for [ray sorted-rays]
    (let [intersections (intersections ray lines)
          closest-t (reduce min (map #(:t %) intersections))]
      (ray/point2d ray closest-t))))

(defn visibility [origin polygons]
  (let [lines (all-lines polygons)
        all-rays (all-rays origin lines)
        sorted-rays (sort-rays all-rays)]
    (visible-path sorted-rays lines)))

(defn benchmark! [f]
  (let [html-element (dom/getElement "stats")
        number-of-samples 51
        samples (atom ())]
    (fn [event]
      (let [before (.now js/Date)
            result (f event)
            dt (- (.now js/Date) before)
            values (swap! samples #(take number-of-samples (cons dt %)))]
        (dom/setTextContent html-element
                            (str
                              (string/format "samples: %02d\n" (count values))
                              (string/format "mean: %.2f ms\n" (statistics/mean values))
                              (string/format "median: %.2f ms" (statistics/median values))))
        result))))

(defn process-event! [event polygons]
  (let [canvas (.-currentTarget event)
        context (dali/context-2d canvas)
        rect (.getBoundingClientRect canvas)
        x (- (.-clientX event) (.-left rect))
        y (- (.-clientY event) (.-top rect))
        visible-path (visibility (vektor/point x y) polygons)]
    (-> context
        (draw-scene! (.-width canvas) (.-height canvas) polygons)
        (dali/fill-path! visible-path)
        (dali/fill-style! "#f00")
        (dali/fill-circle! x y 4))))

(def width 500)
(def height 312)
(def polygons
  (for [points [[{:x -1  :y -1}
                 {:x 500 :y -1}
                 {:x 500 :y 312}
                 {:x -1  :y 312}]
                [{:x 80  :y 100}
                 {:x 160 :y 120}
                 {:x 160 :y 180}
                 {:x 110 :y 200}]
                [{:x 350 :y 250}
                 {:x 300 :y 200}
                 {:x 400 :y 100}
                 {:x 400 :y 170}]
                [{:x 400 :y 20}
                 {:x 380 :y 40}
                 {:x 440 :y 60}]]]
    (vec (for [{:keys [x y]} points]
           (vektor/point x y)))))

(def canvas (dom/getElement "c"))
(def ctx (dali/setup-canvas! canvas width height))

(events/listen canvas "mousemove" (benchmark! #(process-event! % polygons)))
(events/listen canvas "mouseout" #(draw-scene! ctx width height polygons))

(draw-scene! ctx width height polygons)
