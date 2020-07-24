(ns colf.block.animation.blink
  (:require
   [colf.block.core :as b.core]
   [colf.color :as color]))

(defn blink
  [{:keys [blocks color-code]}]
  (let [len (count blocks)
        empty-blocks (repeat len b.core/empty-block)
        fill-blocks (repeat len (color/colorize color-code b.core/fill-block))]
    (concat
     (repeat 5 empty-blocks)
     (repeat 10 fill-blocks)
     (repeat 5 empty-blocks)
     (repeat 10 fill-blocks))))



