(ns colf.block
  (:require
   [clojure.core.async :as async]
   [colf.block.core :as b.core]
   [colf.block.animation :as b.anime]
   [colf.color :as color]
   [com.stuartsierra.component :as component]))

(defn add-color-and-print
  [blocks color-code width fps]
  (let [new-blocks (drop-last (cons (color/colorize color-code b.core/fill-block)
                                    blocks))
        animation-f (rand-nth b.anime/animations)]
    (-> {:before-blocks blocks
         :after-blocks new-blocks
         :color-code color-code
         :width width}
        (animation-f)
        (b.anime/play! width fps))
    new-blocks))

(defrecord Blocks [add-ch exit-ch width height fps]
  component/Lifecycle
  (start [this]
    (let [add-ch (async/chan)
          exit-ch (async/chan)]
      (async/go-loop [blocks (repeat (* width height) b.core/empty-block)]
        (b.core/print-blocks blocks width)
        (async/alt!
          add-ch ([color-code] (recur (add-color-and-print blocks color-code width fps)))
          exit-ch nil))
      (assoc this :add-ch add-ch :exit-ch exit-ch)))
  (stop [this]
    (async/put! exit-ch true)))

(defn new-blocks
  [width height fps]
  (map->Blocks {:width width :height height :fps fps}))
