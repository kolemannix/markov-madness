(ns markov-clj.dickens
  (:use [twitter.oauth])
  (:require [markov-clj.twitter :refer :all]))


(def corpus (clojure.string/split (slurp "resources/twocities.txt") #"\s+"))

(def dickens-creds (apply make-oauth-creds
                     (clojure.string/split-lines
                      (slurp "resources/twitter_auth/dickens_auth.txt"))))

(defn begin-doing [interval]
  (loop []
    (let [tweet (create-tweet [nil corpus])]
      (println "*** tweet\n" tweet)
      (send-tweet tweet dickens-creds))
    (Thread/sleep interval)
    (recur)))

(defn- main [] (begin-doing (* 60 15000)))

(begin-doing (* 60 15000))
