(ns colf.color)

(defn- parse-long
  ([^String s]
   (parse-long s 10))
  ([^String s radix]
   (try
     (Long/parseLong s radix)
     (catch Throwable _ nil))))

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
    (list (parse-long s))

    (re-seq #"^[0-9a-fA-F]{3,6}$" s)
    (->> (normalize-hex s)
         (partition-all 2)
         (map #(apply str %))
         (map #(parse-long % 16)))

    :else []))
