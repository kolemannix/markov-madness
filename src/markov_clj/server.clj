(ns markov-clj.server
  (:require
   [ org.httpkit.server :as http]
   [ markov-clj.github :as github]
   [ compojure.route :as route]
   [ compojure.handler :only [site]]
   [ compojure.core :as cmpj]
   [ ring.middleware.json :as middle]
   [ ring.util.response :as resp]))

(defn generate-commit-response [req]
  (println (-> req :body (get "username")))
  (resp/response (take 5 (github/generate-commits (-> req :body (get "username"))))))

(cmpj/defroutes handler
  (cmpj/GET "/" [] (resp/file-response "/index.html" {:root "public"}))
  (cmpj/GET "/git" [] (resp/file-response "/git.html" {:root "public"}))
  (cmpj/GET "/css/cover.css" [] (resp/file-response "/css/cover.css" {:root "public"}))
  (cmpj/GET "/css/commits.css" [] (resp/file-response "/css/commits.css" {:root "public"}))
  (cmpj/GET "/img/markov_madness.jpg" [] (resp/file-response "/img/markov_madness.jpg" {:root "public"}))
  (cmpj/GET "/js/main.js" [] (resp/file-response "js/main.js" {:root "public"}))
  (cmpj/POST "/commits" [] generate-commit-response)
  (route/resources "/" {:root "public"})
  (route/not-found "<h1>Page not found</h1>"))

(defn logging [chain] (fn [req]
                        (println req)
                        (chain req)))


(def app (-> handler logging middle/wrap-json-body middle/wrap-json-response))

(defn -main [& args] (let [ server (http/run-server app {:port 8080})]))
