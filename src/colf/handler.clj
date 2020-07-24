(ns colf.handler
  (:require
   [clojure.core.async :as async]
   [clojure.string :as str]
   [colf.color :as color]
   [com.stuartsierra.component :as component]))

(defn handle-get
  [action-ch _]
  (let [fetch-ch (async/chan)
        _ (async/put! action-ch [:fetch fetch-ch])
        resp (async/<!! fetch-ch)]
    {:headers {"Content-Type" "application/json"}
     :body resp}))

(defn handle-post
  [action-ch {:keys [uri]}]
  (let [params (drop 1 (str/split uri #"/"))
        color-codes (color/parse-color-code (first params))]
    (cond
      (empty? params)
      "No color-code"

      (or (some nil? color-codes)
          (not (#{1 3} (count color-codes))))
      (format "Invalid color-code: %s" (pr-str params))

      :else
      (do (async/put! action-ch [:add color-codes])
          "OK"))))

(defn handler*
  [action-ch {:keys [method] :as req}]
  (case method
    "GET" (handle-get action-ch req)
    "POST" (handle-post action-ch req)
    "Not supported"))

(defrecord Handler [blocks handler]
  component/Lifecycle
  (start [this]
    (assoc this :handler (partial handler* (:action-ch blocks))))
  (stop [this]))

(defn new-handler
  []
  (map->Handler {}))
