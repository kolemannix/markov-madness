(ns markov-clj.twitter
  (:gen-class)
  (:use
   [twitter.oauth]
   [twitter.callbacks]
   [twitter.callbacks.handlers]
   [twitter.api.restful]
   [twitter.request]
   [twitter.api.streaming]
   [clojure.data.json :as json ]
   [markov-clj.markov :as markov]
   [clojure.set :as set]
   )
  (:require
   [http.async.client :as ac])
  (:import
   (twitter.callbacks.protocols SyncSingleCallback)
   (twitter.callbacks.protocols AsyncStreamingCallback)))


(def my-creds (apply make-oauth-creds
                     (clojure.string/split-lines
                      (slurp "resources/twitter_auth/markov_auth.txt"))))

; simply retrieves the user, authenticating with the above credentials
; note that anything in the :params map gets the -'s converted to _'s

(defn- get-page [screen-name]
  (:body (statuses-user-timeline :oauth-creds my-creds
                                 :params {:screen-name screen-name 
                                          :count 200
                                          :include-rts "false"})))

(defn- get-tweets [screen-name] (->>
                               (get-page screen-name)
                               (map :text)))

(defn tweet-the-twitter [text]
  (try (statuses-update :oauth-creds my-creds
                        :params {:status text})
       (catch Exception e (println "Bitch I ain't stopping for no error.")))) 

(defn store-followers [ids]
  (->>
   (interpose " " ids)
   (apply str)
   (spit "followers.txt"))
  ids)

(defn find-new-followers [current]
  (let [old-fol (map read-string (re-seq #"\w+" (slurp "followers.txt")))
        current-fol (into #{} current)]
    (->>
     (into #{} old-fol)
     (clojure.set/difference current-fol))))

(defn make-valid-tweet [screen-name tweets]
  (let [candidate-tweets (take 20 (markov-clj.markov/generate (apply str (interpose " " tweets)) 1))
        suffix (str " - @" screen-name)]
    (loop [candidate (first candidate-tweets)
           others (rest candidate-tweets)]
      (let [final-tweet (str candidate suffix)]
        (if (< (count final-tweet) 140)
          final-tweet
          (if (empty? others)
            nil
            (recur (first others) (rest others))))))))


(defn create-and-send-tweet [[screen-name tweets]]
  (->> (make-valid-tweet screen-name tweets)
       tweet-the-twitter
       )
  )

(defn reply [new-followers]
  (let [follower  (->> new-followers
                       seq
                       rand-nth)
        the-vec [follower (get-tweets follower)]]
    (create-and-send-tweet the-vec)))

(defn store-and-reply-to-followers [handles]
  (reply (find-new-followers handles)))

(defn process-followers []
  (->> (followers-list :oauth-creds my-creds
                       :params {:screen-name "mockingmarkov"})
       :body
       :users
       (map :screen_name)
       store-and-reply-to-followers
       ))

(defn mock-user [handle]
  (let [tweets (get-tweets handle)]
    (create-and-send-tweet [handle tweets]))

(defn start-markov-thread []
  (loop []
    (Thread/sleep (* 60 15000))
    (process-followers)
    (recur)))

(defn -main [] (start-markov-thread))

;; (markov-clj.util/tick-now 500 #(println "Hey there!"))

;; (process-followers)
