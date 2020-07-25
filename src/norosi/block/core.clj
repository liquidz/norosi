(ns norosi.block.core
  (:require
   [norosi.color :as color]))

(def fill-block {:color [] :content \u25A0})
(def empty-block {:color [] :content \u25A1})

(defn cursor-up
  [n]
  (print (str \u001b "[" n "F")))

(defn print-blocks
  [blocks width]
  (let [lines (partition width blocks)]
    (doseq [line lines]
      (let [colored-line (map #(color/colorize (:color %) (:content %)) line)]
        (println (apply str (interleave colored-line (repeat \space))))))
    (cursor-up (count lines))))
