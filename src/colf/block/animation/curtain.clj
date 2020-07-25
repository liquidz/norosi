(ns colf.block.animation.curtain
  (:require
   [colf.block.core :as b.core]))

(defn- curtain-out*
  [{:keys [blocks width color-code]}]
  (let [lines (partition width blocks)
        frame-num (int (/ width 2))
        fill-block (assoc b.core/fill-block :color color-code)]
    (loop [[i & next-i] (range frame-num)
           lines lines
           result []]
      (if-not i
        (conj result (repeat (count blocks) fill-block))
        (let [next-lines (map (fn [l]
                                (-> (vec l)
                                    (update i (constantly fill-block))
                                    (update (- width (inc i)) (constantly fill-block))))
                              lines)]
          (recur next-i next-lines (conj result (flatten next-lines))))))))

(defn curtain-out
  [m]
  (let [frames (curtain-out* m)]
    (apply interleave (repeat 2 frames))))

(def curtain-in (comp reverse curtain-out))

(comment
(colf.block.animation/test-play! curtain-out))


