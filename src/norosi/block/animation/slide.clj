(ns norosi.block.animation.slide
  (:require
   [norosi.block.core :as b.core]))

(defn slide-out
  [{:keys [blocks width color-code]}]
  (let [lines (partition width blocks)
        height (count lines)
        frame-num (+ (inc width) (dec height))]
    (for [i (range 1 frame-num)]
      (flatten
       (map-indexed
        (fn [j line]
          (let [[head tail] (split-at (- i j) line)]
            (concat (repeat (count head) (assoc b.core/fill-block :color color-code))
                    tail)))
        lines)))))

(def slide-in (comp reverse slide-out))
