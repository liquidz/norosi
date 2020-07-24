(ns colf.block.animation
  (:require
   [colf.block.animation.blink :as b.anime.blink]
   [colf.block.animation.mosaic :as b.anime.mosaic]
   [colf.block.animation.slide :as b.anime.slide]
   [colf.block.core :as b.core]))

(defn slide-and-blink
  [{:keys [blocks new-blocks color-code width]}]
  (let [m {:blocks blocks :width width :color-code color-code}]
    (concat
     (b.anime.slide/slide-out m)
     (b.anime.blink/blink m)
     (b.anime.slide/slide-in (assoc m :blocks new-blocks)))))

(defn mosaic-and-blink
  [{:keys [blocks new-blocks color-code width]}]
  (let [m {:blocks blocks :width width :color-code color-code}]
    (concat
     (b.anime.mosaic/mosaic-out m)
     (b.anime.blink/blink m)
     (b.anime.mosaic/mosaic-in (assoc m :blocks new-blocks)))))

(def animations
  [slide-and-blink
   mosaic-and-blink])

(defn play!
  [frames width fps]
  (let [sleep-msec (int (/ 1000 fps))]
    (doseq [f frames]
      (b.core/print-blocks f width)
      (Thread/sleep sleep-msec))))

(defn random-play!
  [{:keys [width fps] :as m}]
  (let [animation-f (rand-nth animations)]
    (-> (animation-f m)
        (play! width fps))))

(defn test-play!
  [animation-f]
  (let [width 5
        height 2
        test-blocks (repeat (* width height) b.core/empty-block)]
    (doseq [f (animation-f {:blocks test-blocks :width width :color-code [31]})]
      (with-redefs [b.core/cursor-up (constantly nil)]
        (b.core/print-blocks f width))
      (println "..."))))
