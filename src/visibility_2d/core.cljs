(ns visibility-2d.core
  (:require [goog.dom :as dom]
            [goog.dom.ViewportSizeMonitor :as ViewportSizeMonitor]
            [goog.events :as events]
            [goog.events.EventType :as EventType]
            [goog.string :as string]
            [goog.string.format]
            [visibility-2d.ray :as ray]
            [visibility-2d.dali :as dali]
            [visibility-2d.vektor :as vektor]
            [visibility-2d.statistics :as statistics]))

;;; Global state

(defonce canvas (dom/getElement "c"))
(defonce polygons (atom))

(defn reset-polygons! [w h]
  (let [outline [{:x (dec 0) :y (dec 0)}
                 {:x (inc w) :y (dec 0)}
                 {:x (inc w) :y (inc h)}
                 {:x (dec 0) :y (inc h)}]]
    (reset! polygons
            (for [points [outline
                          [{:x  80 :y 100}
                           {:x 160 :y 120}
                           {:x 160 :y 180}
                           {:x 110 :y 200}]
                          [{:x 350 :y 250}
                           {:x 300 :y 200}
                           {:x 400 :y 100}
                           {:x 400 :y 170}]
                          [{:x 400 :y  20}
                           {:x 380 :y  40}
                           {:x 440 :y  60}]]]
              (vec (for [{:keys [x y]} points]
                     (vektor/point x y)))))))

;;; Visibility

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
  "Sorts rays based on their angles."
  (sort (comparator #(< (ray/angle2d %1) (ray/angle2d %2))) rays))

(defn all-lines [polygons]
  "Returns a list of lines (start and end points) that make up a polygon."
  (mapcat #(map list % (vektor/shift-vector %)) polygons))

(defn intersections [ray lines]
  "Returns a list of all intersections between ray and lines."
  (for [line lines
        :let [ray-line-intersection (intersection ray line)
              {:keys [t u]} ray-line-intersection]
        :when (and (> t 0) (> u 0) (<= u 1))]
    ray-line-intersection))

(defn all-rays [origin lines]
  (let [epsilon 0.00175] ; How did I come up with this value again?
    (for [line lines
          point line
          angle [0 (- epsilon) epsilon]]
      (ray/ray origin point angle))))

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

;;; Benchmarking

(defn benchmark! [f]
  (let [html-element (dom/getElement "stats")
        number-of-samples 51
        samples (atom ())]
    (fn [event]
      (let [before (.now js/Date)
            result (f event)
            dt (- (.now js/Date) before)
            values (swap! samples #(take number-of-samples (cons dt %)))]
        (set! (.-hidden html-element) js/false)
        (dom/setTextContent
          html-element
          (str
            (string/format "samples: %02d\n" (count values))
            (string/format "mean: %.2f ms\n" (statistics/mean values))
            (string/format "median: %.2f ms" (statistics/median values))))
        result))))

;;; Drawing

(defn resize-canvas! [canvas]
  "Resizes the canvas to its parent's size and returns the context."
  (let [parent (.-parentElement canvas)
        width  (.-clientWidth parent)
        height (.-clientHeight parent)]
    (reset-polygons! width height)
    (dali/setup-canvas! canvas width height)))

(defn draw-scene! [ctx polygons]
  "Draws the polygons into the canvas' context."
  (let [canvas (.-canvas ctx)
        w (.-width canvas)
        h (.-height canvas)]
    (-> ctx
        (dali/fill-style! "#222")
        (dali/fill-rect! {:x 0 :y 0 :w w :h h})
        (dali/stroke-style! "white")
        (dali/stroke-paths! polygons)
        (dali/fill-style! "#ddd"))))

;;; Event Handling

(defn handle-move! [event]
  (let [canvas (.-currentTarget event)
        ctx (dali/context-2d canvas)
        rect (.getBoundingClientRect canvas)
        x (- (.-clientX event) (.-left rect))
        y (- (.-clientY event) (.-top rect))
        visible-path (visibility (vektor/point x y) @polygons)]
    (.preventDefault event)
    (-> ctx
        (draw-scene! @polygons)
        (dali/fill-path! visible-path)
        (dali/fill-style! "yellow")
        (dali/fill-circle! x y 4))))

(defn handle-up! [event]
  (let [canvas (.-currentTarget event)
        ctx (dali/context-2d canvas)]
    (.preventDefault event)
    (draw-scene! ctx @polygons)))

(defn handle-resize! [event]
  (draw-scene! (resize-canvas! canvas) @polygons))

(defn handle-event! [event]
  (case (.-type event)
    ("mousemove" "touchstart" "touchmove") (handle-move! event)
    ("mouseout" "touchend") (handle-up! event)
    ("resize") (handle-resize! event)))

; Set up event listeners
(events/listen (dom/ViewportSizeMonitor.) EventType/RESIZE handle-event!)
(events/listen canvas EventType/MOUSEOUT handle-event!)
(events/listen canvas EventType/MOUSEMOVE handle-event!)
(events/listen canvas EventType/TOUCHSTART handle-event!)
(events/listen canvas EventType/TOUCHMOVE handle-event!)
(events/listen canvas EventType/TOUCHEND handle-event!)

; Initial drawing
(draw-scene! (resize-canvas! canvas) @polygons)
