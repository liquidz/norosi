(ns norosi.block.animation
  (:require
   [norosi.block.animation.blink :as b.anime.blink]
   [norosi.block.animation.curtain :as b.anime.curtain]
   [norosi.block.animation.mosaic :as b.anime.mosaic]
   [norosi.block.animation.slide :as b.anime.slide]
   [norosi.block.core :as b.core]))

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

(defn curtain-and-blink
  [{:keys [blocks new-blocks color-code width]}]
  (let [m {:blocks blocks :width width :color-code color-code}]
    (concat
     (b.anime.curtain/curtain-out m)
     (b.anime.blink/blink m)
     (b.anime.curtain/curtain-in (assoc m :blocks new-blocks)))))

(def animations
  [slide-and-blink
   mosaic-and-blink
   curtain-and-blink])

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
        test-blocks (repeat (* width height) b.core/empty-block)
        result (animation-f {:blocks test-blocks :width width :color-code [31]})]
    (try
      (doseq [f result]
        (with-redefs [b.core/cursor-up (constantly nil)]
          (b.core/print-blocks f width))
        (println "..."))

      (catch Exception ex
        (println "Result:" result)
        (println "Failed to play:" (.getMessage ex))))))
