(ns visibility-2d.statistics)

(defn mean [values]
  {:pre  [(coll? values)
          (every? number? values)
          (not (empty? values))]
   :post [(number? %)]}
  (/ (reduce + values) (count values)))

(defn median [values]
  {:pre  [(coll? values)
          (every? number? values)
          (not (empty? values))]
   :post [(number? %)]}
  (let [sorted-values (sort values)
        number-of-values (count values)
        index (Math/floor (/ number-of-values 2))
        midpoint (nth sorted-values index)]
    (if (even? number-of-values)
      (mean [midpoint (nth sorted-values (dec index))])
      midpoint)))
