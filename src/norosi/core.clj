(ns norosi.core
  (:gen-class)
  (:require
   [clojure.data.json :as json]
   [clojure.tools.cli :as cli]
   [com.stuartsierra.component :as component]
   [norosi.block :as block]
   [norosi.handler :as handler]
   [norosi.http :as http]
   [norosi.util :as util]))

(def cli-options
  [["-w" "--width WIDTH" "Blocks width" :default 20 :parse-fn util/parse-long]
   ["-h" "--height HEIGHT" "Blocks height" :default 5 :parse-fn util/parse-long]
   ["-s" "--fps FPS" "FIXME" :default 100 :parse-fn util/parse-long]
   ["-i" "--import JSON" "FIXME"]
   ["-p" "--port PORT" "Port number" :default 8000 :parse-fn util/parse-long]
   [nil "--help"]])

(defn- import-json
  [exported-file]
  (-> exported-file
      (slurp)
      (json/read-str :key-fn keyword)
      (update :blocks (fn [blks] (map #(update % :content first) blks)))))

(defn new-system
  [profile {:keys [width height import fps port]}]
  (let [imported (and import (import-json import))
        width (:width imported width)
        height (:height imported height)]
    (component/system-map
     :blocks (block/new-blocks (:blocks imported) width height fps)
     :handler (component/using
               (handler/new-handler)
               [:blocks])
     :server (component/using
              (http/new-server {:port port
                                :join? (= :prod profile)})
              [:handler]))))

(defn -main
  [& args]
  (let [{:keys [options summary errors]} (cli/parse-opts args cli-options)
        {:keys [port help]} options]
    (cond
      errors (doseq [e errors] (println e))
      help (println (str "Usage:\n" summary))
      :else (do (println (format "# HTTP server will start on port %d" port))
                (println (format "# e.g. curl -XPOST http://localhost:%d/51A8DD" port))
                (component/start-system
                 (new-system :prod options))))))
