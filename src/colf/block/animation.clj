(ns colf.block.animation
  (:require
   [colf.block.animation.blink :as b.anime.blink]
   [colf.block.animation.mosaic :as b.anime.mosaic]
   [colf.block.animation.slide :as b.anime.slide]
   [colf.block.core :as b.core]))

(defn slide-and-blink
  [{:keys [before-blocks after-blocks color-code width]}]
  (let [m {:blocks before-blocks :width width :color-code color-code}]
    (concat
     (b.anime.slide/slide-out m)
     (b.anime.blink/blink m)
     (b.anime.slide/slide-in (assoc m :blocks after-blocks)))))

(defn mosaic-and-blink
  [{:keys [before-blocks after-blocks color-code width]}]
  (let [m {:blocks before-blocks :width width :color-code color-code}]
    (concat
     (b.anime.mosaic/mosaic-out m)
     (b.anime.blink/blink m)
     (b.anime.mosaic/mosaic-in (assoc m :blocks after-blocks)))))

(def animations
  [slide-and-blink
   mosaic-and-blink]
  )

(defn play!
  [frames width fps]
  (let [sleep-msec (int (/ 1000 fps))]
    (doseq [f frames]
      (b.core/print-blocks f width)
      (Thread/sleep sleep-msec))))
