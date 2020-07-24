(ns colf.block.core)

(def fill-block \u25A0)
(def empty-block \u25A1)

(defn cursor-up
  [n]
  (print (str \u001b "[" n "F")))

(defn print-blocks
  [blocks width]
  (let [lines (partition width blocks)]
    (doseq [line lines]
      (println (apply str (interleave line (repeat \space)))))
    (cursor-up (count lines))))
