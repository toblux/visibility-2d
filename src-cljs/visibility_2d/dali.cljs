(ns visibility-2d.dali
  (:require [goog.dom :as dom]))

(defn- rect? [{:keys [x y w h]}]
  "Private function that checks for a rect."
  (and (number? x) (number? y) (number? w) (number? h)))

(defn context-2d [canvas]
  "Returns the 2D context from a canvas element."
  (.getContext canvas "2d"))

(defn setup-canvas!
  "Sets up a canvas element by scaling it and its context to the right size."
  [canvas w h]
  {:pre [(number? w) (number? h)]}
  (let [ctx (context-2d canvas)
        scale-factor (dom/getPixelRatio)]
    (set! (.-width canvas) (* w scale-factor))
    (set! (.-height canvas) (* h scale-factor))
    (set! (.-width (.-style canvas)) (str w "px"))
    (set! (.-height (.-style canvas)) (str h "px"))
    (set! (.-minWidth (.-style canvas)) (str w "px"))
    (set! (.-minHeight (.-style canvas)) (str h "px"))
    (.scale ctx scale-factor scale-factor)
    ctx))

(defn fill-style! [ctx color]
  "Sets the fill style of a context to the specified color."
  (set! (.-fillStyle ctx) color)
  ctx)

(defn stroke-style! [ctx color]
  "Sets the stroke style of a context to the specified color."
  (set! (.-strokeStyle ctx) color)
  ctx)

(defn fill-rect! [ctx rect]
  "Color fills a rect in the given context by the currently set fill color (see `fill-style!`)."
  {:pre  [(rect? rect)]
   :post [(= ctx %)]}
  (let [{:keys [x y w h]} rect]
    (.fillRect ctx x y w h))
  ctx)

(defn stroke-path! [ctx path]
  {:post [(= ctx %)]}
  (doto ctx
    (.beginPath)
    (#(let [{:keys [x y]} (first path)] (.moveTo % x y)))
    (#(doseq [{:keys [x y]} (rest path)] (.lineTo % x y)))
    (.closePath)
    (.stroke)))

(defn stroke-paths! [ctx paths]
  {:post [(= ctx %)]}
  (doseq [path paths] (stroke-path! ctx path))
  ctx)

(defn fill-path! [ctx path]
  {:post [(= ctx %)]}
  (doto ctx
    (.beginPath)
    (#(let [{:keys [x y]} (first path)] (.moveTo % x y)))
    (#(doseq [{:keys [x y]} (rest path)] (.lineTo % x y)))
    (.closePath)
    (.fill)))

(defn fill-paths! [ctx paths]
  {:post [(= ctx %)]}
  (doseq [path paths] (fill-path! ctx path))
  ctx)

(defn fill-circle! [ctx x y radius]
  {:pre [(number? x) (number? y) (number? radius)]
   :post [(= ctx %)]}
  (doto ctx
    (.beginPath)
    (.arc x y radius 0 (* 2 (.-PI js/Math)))
    (.closePath)
    (.fill)))

(defn stroke-line! [ctx {x1 :x y1 :y} {x2 :x y2 :y}]
  {:post [(= ctx %)]}
  (doto ctx
    (.beginPath)
    (.moveTo x1 y1)
    (.lineTo x2 y2)
    (.stroke)))
