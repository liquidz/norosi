(ns colf.block
  (:require
   [clojure.core.async :as async]
   [com.stuartsierra.component :as component]))

(def fill-block \u25A0)
(def empty-block \u25A1)

(defn color
  [code s]
  (case (count code)
    1 (str \u001b "[" (first  code) "m" s \u001b "[m")
    3 (let [[r g b] code]
        (str \u001b "[" 38 ";2;" r ";" g ";" b "m" s \u001B "[m"))))

(defn cursor-up
  [n]
  (print (str \u001b "[" n "F")))

(defn print-blocks
  [blocks width]
  (let [lines (partition width blocks)]
    (doseq [line lines]
      (println (apply str (interleave line (repeat \space)))))
    (cursor-up (count lines))))

(defn fade-out-animation
  [blocks color-code width]
  (let [lines (partition width blocks)
        height (count lines)
        frame-num (+ (inc width) (dec height))
        sleep-msec (int (/ 100 frame-num))]
    (doseq [i (range 1 frame-num)]
      (print-blocks
       (flatten
        (map-indexed
         (fn [j line]
           (let [[head tail] (split-at (- i j) line)]
             (concat (repeat (count head) (color color-code fill-block))
                     tail)))
         lines))
       width)
      (Thread/sleep sleep-msec))))

(defn blinking-animation
  [blocks color-code width]
  (let [empty-blocks (repeat (count blocks) empty-block)
        color-blocks (repeat (count blocks) (color color-code fill-block))]
    (dotimes [_ 2]
      (print-blocks empty-blocks width)
      (Thread/sleep 200)
      (print-blocks color-blocks width)
      (Thread/sleep 200))
    (Thread/sleep 200)))

(defn fade-in-animation
  [blocks color-code width]
  (let [lines (partition width blocks)
        height (count lines)
        frame-num (+ (inc width) (dec height))
        sleep-msec (int (/ 100 frame-num))]
    (doseq [i (reverse (range frame-num))]
      (print-blocks
       (flatten
        (map-indexed
         (fn [j line]
           (let [[head tail] (split-at (- i j) line)]
             (concat (repeat (count head) (color color-code fill-block))
                     tail)))
         lines))
       width)
      (Thread/sleep sleep-msec))))

(defn add-color-and-print
  [blocks color-code width]
  (let [new-blocks (drop-last (cons (color color-code fill-block) blocks))]
    (fade-out-animation blocks color-code width)
    (Thread/sleep 1000)
    (blinking-animation blocks color-code width)
    (fade-in-animation new-blocks color-code width)
    (Thread/sleep 1000)
    new-blocks))

(defrecord Blocks [add-ch exit-ch width height]
  component/Lifecycle
  (start [this]
    (let [add-ch (async/chan)
          exit-ch (async/chan)]
      (async/go-loop [blocks (repeat (* width height) empty-block)]
        (print-blocks blocks width)
        (async/alt!
          add-ch ([color-code] (recur (add-color-and-print blocks color-code width)))
          exit-ch nil))
      (assoc this :add-ch add-ch :exit-ch exit-ch)))
  (stop [this]
    (async/put! exit-ch true)))

(defn new-blocks
  [width height]
  (map->Blocks {:width width :height height}))
