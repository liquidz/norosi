(ns norosi.util)

(defn parse-long
  ([^String s]
   (parse-long s 10))
  ([^String s radix]
   (try
     (Long/parseLong s radix)
     (catch Throwable _ nil))))
