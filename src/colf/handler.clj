(ns colf.handler
  (:require
   [clojure.core.async :as async]
   [clojure.string :as str]
   [colf.color :as color]
   [com.stuartsierra.component :as component]))

(defn handler*
  [add-ch {:keys [uri]}]
  (let [params (drop 1 (str/split uri #"/"))
        color-codes (color/parse-color-code (first params))]
    (cond
      (empty? params)
      "No color-code"

      (or (some nil? color-codes)
          (not (#{1 3} (count color-codes))))
      (format "Invalid color-code: %s" (pr-str params))

      :else
      (do (async/put! add-ch color-codes)
          "OK"))))

(defrecord Handler [blocks handler]
  component/Lifecycle
  (start [this]
    (assoc this :handler (partial handler* (:add-ch blocks))))
  (stop [this]))

(defn new-handler
  []
  (map->Handler {}))
