(ns norosi.block.animation.mosaic
  (:require
   [norosi.block.core :as b.core]))

(defn mosaic-out
  [{:keys [blocks color-code]}]
  (let [frame-num 30
        block-len (count blocks)
        n (max 1 (int (/ block-len frame-num)))
        all-indexes (shuffle (range block-len))]
    (loop [[indexes & rest-indexes] (partition-all n all-indexes)
           blocks blocks
           result []]
      (if-not indexes
        result
        (let [next-blocks
              (reduce
               (fn [v idx]
                 (update v idx (fn [_] (assoc b.core/fill-block :color color-code))))
               (vec blocks)
               indexes)]
          (recur rest-indexes next-blocks (conj result next-blocks)))))))

(def mosaic-in (comp reverse mosaic-out))

(comment
 (norosi.block.animation/test-play! mosaic-out)
 (norosi.block.animation/test-play! mosaic-in))
