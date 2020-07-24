(ns colf.block.animation.slide
  (:require
   [colf.block.core :as b.core]
   [colf.color :as color]))

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
            (concat (repeat (count head) (color/colorize color-code b.core/fill-block))
                    tail)))
        lines)))))

(def slide-in (comp reverse slide-out))
