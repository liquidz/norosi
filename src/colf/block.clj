(ns colf.block
  (:require
   [clojure.core.async :as async]
   [clojure.data.json :as json]
   [colf.block.animation :as b.anime]
   [colf.block.core :as b.core]
   [com.stuartsierra.component :as component]))

(defmulti process-action
  (fn [action _m]
    (first action)))

(defmethod process-action :default
  [_ {:keys [blocks]}]
  blocks)

(defmethod process-action :add
  [[_ color-code chr] {:keys [blocks] :as m}]
  (let [block (cond-> b.core/fill-block
                chr (assoc :content chr)
                true (assoc :color color-code))
        new-blocks (drop-last (cons block blocks))]
    (b.anime/random-play!
     (merge m {:color-code color-code
               :new-blocks new-blocks}))
    new-blocks))

(defmethod process-action :fetch
  [[_ fetch-ch] {:keys [blocks width height]}]
  (->> {:blocks (map #(update % :content str) blocks)
        :width width
        :height height}
       (json/write-str)
       (async/put! fetch-ch))
  blocks)

(defrecord Blocks [action-ch exit-ch initial-blocks width height fps]
  component/Lifecycle
  (start [this]
    (let [action-ch (async/chan)
          exit-ch (async/chan)]
      (async/go-loop [blocks (or initial-blocks
                                 (repeat (* width height) b.core/empty-block))]
        (b.core/print-blocks blocks width)
        (async/alt!
          action-ch ([action]
                     (let [m {:blocks blocks :width width :height height :fps fps}]
                       (recur (process-action action m))))
          exit-ch nil))
      (assoc this :action-ch action-ch :exit-ch exit-ch)))
  (stop [this]
    (async/put! exit-ch true)))

(defn new-blocks
  [initial-blocks width height fps]
  (map->Blocks {:initial-blocks initial-blocks :width width :height height :fps fps}))
