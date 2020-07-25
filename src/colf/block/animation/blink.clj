(ns colf.block.animation.blink
  (:require
   [colf.block.core :as b.core]))

(defn- blink*
  [{:keys [blocks color-code]}]
  (let [len (count blocks)
        empty-blocks (repeat len b.core/empty-block)
        fill-blocks (repeat len (assoc b.core/fill-block :color color-code))]
    [fill-blocks fill-blocks
     empty-blocks
     fill-blocks fill-blocks
     empty-blocks
     fill-blocks fill-blocks]))

(defn blink
  [m]
  (let [frames (blink* m)]
    (apply interleave (repeat 10 frames))))

(comment
 (colf.block.animation/test-play! blink))
