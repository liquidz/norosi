(ns user
  (:require
   [colf.core :as core]
   [com.stuartsierra.component :as component]))

(defonce system (atom nil))

(def ^:private dev-opts
  {:width 5 :height 2 :fps 50 :port 8009})

(defn start
  []
  (when-not @system
    (->> (core/new-system :dev dev-opts)
         (component/start-system)
         (reset! system))
    ::started))

(defn stop
  []
  (when @system
    (component/stop-system @system)
    (reset! system nil)
    ::stopped))

(defn go
  []
  (stop)
  (start))
