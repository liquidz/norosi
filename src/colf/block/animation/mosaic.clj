(ns colf.block.animation.mosaic
  (:require
   [colf.block.core :as b.core]
   [colf.color :as color]))

(defn mosaic-out
  [{:keys [blocks color-code]}]
  (let [frame-num 30
        block-len (count blocks)
        n (int (/ block-len frame-num))
        all-indexes (shuffle (range block-len))]
    (loop [[indexes & rest-indexes] (partition-all n all-indexes)
           blocks blocks
           result []]
      (if-not indexes
        result
        (let [next-blocks
              (reduce
               (fn [v idx]
                 (update v idx (fn [_] (color/colorize color-code b.core/fill-block))))
               (vec blocks)
               indexes)]
          (recur rest-indexes next-blocks (conj result next-blocks)))))))

(def mosaic-in (comp reverse mosaic-out))
