(ns colf.http
  (:require
   [com.stuartsierra.component :as component])
  (:import
   (com.sun.net.httpserver
    Headers
    HttpExchange
    HttpHandler
    HttpServer)
   (java.io
    OutputStream)
   java.net.InetSocketAddress
   (java.nio.charset
    StandardCharsets)
   (java.time
    ZoneOffset
    ZonedDateTime)
   (java.time.format
    DateTimeFormatter)
   (java.util.concurrent
    Executors)))

(defn- content-length
  [^String s]
  (if (string? s)
    (count (.getBytes s StandardCharsets/UTF_8))
    0))

(defn- send-response!
  [^HttpExchange t ^long status ^String body]
  (.sendResponseHeaders t status (content-length body))
  (with-open [^OutputStream os (.getResponseBody t)]
    (doto os
      (.write (.getBytes body StandardCharsets/UTF_8))
      (.flush))))

(defn- new-http-handler
  [handler]
  (reify HttpHandler
    (^void handle [this ^HttpExchange t]
      (try
        (let [req {:uri (str (.getRequestURI t))
                   :method (.getRequestMethod t)
                   :protocol (.getProtocol t)}
              res (handler req)
              res (if (map? res)
                    res
                    {:body (str res)})

              ^Headers res-header (.getResponseHeaders t)]
          (doto res-header
            (.set "Content-Type" (get-in res [:headers "Content-Type"] "text/plain"))
            (.set "Last-Modified" (.. (ZonedDateTime/now ZoneOffset/UTC)
                                      (format (DateTimeFormatter/RFC_1123_DATE_TIME))))
            (.set "Server" "colf"))

          (send-response! t (get res :status 200) (:body res)))
        (catch Exception ex
          (send-response! t 500 (.getMessage ex)))))))

(defrecord Server [^HttpServer server handler opts]
  component/Lifecycle
  (start [this]
    (let [port (:port opts 8000)
          server (HttpServer/create (InetSocketAddress. port) 0)]
      (doto server
        (.createContext "/" (new-http-handler (:handler handler)))
        (.setExecutor (Executors/newCachedThreadPool)))
      (if (:join? opts true)
        (.start server)
        (.start (Thread. (fn [] (.start server)))))
      (assoc this :server server)))

  (stop [this]
    (when server
      (.stop server 0))))

(defn new-server
  [opts]
  (map->Server {:opts opts}))
