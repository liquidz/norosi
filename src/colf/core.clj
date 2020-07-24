(ns colf.core
  (:gen-class)
  (:require
   [clojure.tools.cli :as cli]
   [colf.block :as block]
   [colf.handler :as handler]
   [colf.http :as http]
   [com.stuartsierra.component :as component]))

(defn- parse-long
  [^String s]
  (Long/parseLong s))

(def cli-options
  [["-w" "--width WIDTH" "Blocks width" :default 20 :parse-fn parse-long]
   ["-h" "--height HEIGHT" "Blocks height" :default 5 :parse-fn parse-long]
   ["-s" "--fps FPS" "FIXME" :default 100 :parse-fn parse-long]
   ["-p" "--port PORT" "Port number" :default 8000 :parse-fn parse-long]
   [nil "--help"]])

(defn new-system
  [profile {:keys [width height fps port]}]
  (component/system-map
   :blocks (block/new-blocks width height fps)
   :handler (component/using
             (handler/new-handler)
             [:blocks])
   :server (component/using
            (http/new-server {:port port
                              :join? (= :prod profile)})
            [:handler])))

(defn -main
  [& args]
  (let [{:keys [options summary errors]} (cli/parse-opts args cli-options)
        {:keys [port help]} options]
    (cond
      errors (doseq [e errors] (println e))
      help (println (str "Usage:\n" summary))
      :else (do (println (format "# HTTP server will start on port %d" port))
                (println (format "# e.g. curl http://localhost:%d/51A8DD" port))
                (component/start-system
                 (new-system :prod options))))))
