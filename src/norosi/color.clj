(ns norosi.color
  (:require
   [norosi.util :as util]))

(defn- normalize-hex
  [^String s]
  (if (= 3 (count s))
    (->> (seq s)
         (mapcat #(repeat 2 %))
         (apply str))
    s))

(defn parse-color-code
  [^String s]
  (cond
    (re-seq #"^\d{2}$" s)
    (list (util/parse-long s))

    (re-seq #"^[0-9a-fA-F]{3,6}$" s)
    (->> (normalize-hex s)
         (partition-all 2)
         (map #(apply str %))
         (map #(util/parse-long % 16)))

    :else []))

(defn colorize
  [color-codes s]
  (case (count color-codes)
    0 s
    1 (str \u001b "[" (first  color-codes) "m" s \u001b "[m")
    3 (let [[r g b] color-codes]
        (str \u001b "[" 38 ";2;" r ";" g ";" b "m" s \u001B "[m"))))
