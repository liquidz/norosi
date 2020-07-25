(ns user
  (:require
   [clojure.data.json :as json]
   [com.stuartsierra.component :as component]
   [norosi.block.core :as b.core]
   [norosi.color :as color]
   [norosi.core :as core]))

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

(def logo
  (let [_ (assoc b.core/empty-block :color (color/parse-color-code "727171"))
        a (assoc b.core/fill-block :color (color/parse-color-code "51A8DD"))
        c (assoc b.core/fill-block :color (color/parse-color-code "D0104C"))
        e (assoc b.core/fill-block :color (color/parse-color-code "FFB11B"))
        m (assoc b.core/fill-block :color (color/parse-color-code "7BA23F"))
        n (assoc b.core/fill-block :color (color/parse-color-code "6A4C9C"))]
    {:width 26
     :height 5
     :blocks [_ a _ _ a _ c c c _ e e e _ c c c _ m m m _ n n n _
              _ a a _ a _ c _ c _ e _ e _ c _ c _ m _ _ _ _ n _ _
              _ a a _ a _ c _ c _ e e e _ c _ c _ m m m _ _ n _ _
              _ a _ a a _ c _ c _ e e _ _ c _ c _ _ _ m _ _ n _ _
              _ a _ _ a _ c c c _ e _ e _ c c c _ m m m _ n n n _]}))

(comment
(->> (update logo :blocks (fn [blocks] (map #(update % :content str) blocks)))
     (json/write-str)
     (spit "logo.json")))
