(ns colf.block.animation.blink
  (:require
   [colf.block.core :as b.core]))

(defn blink
  [{:keys [blocks color-code]}]
  (let [len (count blocks)
        empty-blocks (repeat len b.core/empty-block)
        fill-blocks (repeat len (assoc b.core/fill-block :color color-code))]
    (concat
     (repeat 5 empty-blocks)
     (repeat 10 fill-blocks)
     (repeat 5 empty-blocks)
     (repeat 10 fill-blocks))))

(comment
 (colf.block.animation/test-play! blink))
