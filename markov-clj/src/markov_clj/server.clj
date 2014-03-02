(ns markov-clj.server
  (:require
   [ org.httpkit.server :as http]
   [ markov-clj.github :as github]
   [ compojure.route :as route]
   [ compojure.handler :only [site]]
   [ compojure.core :as cmpj]
   [ ring.middleware.json :as middle]
   [ ring.util.response :as resp]))

(defn generate-commit-response [req])

(cmpj/defroutes handler
  (cmpj/GET "/" [] resp/file-response "/index.html" {:root "/public"})
  (cmpj/POST "/commits" [] generate-commit-response)
  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))

(defn logging [chain] (fn [req]
                        (println req)
                        (chain req)))


(def app (-> handler logging middle/wrap-json-body middle/wrap-json-response))

(defn -main [& args] (let [ server (http/run-server app {:port 8080})]))
