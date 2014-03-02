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


(def markov-creds (apply make-oauth-creds
                     (clojure.string/split-lines
                      (slurp "resources/twitter_auth/markov_auth.txt"))))

; simply retrieves the user, authenticating with the above credentials
; note that anything in the :params map gets the -'s converted to _'s

(defn- get-page [screen-name]
  (:body (statuses-user-timeline :oauth-creds markov-creds
                                 :params {:screen-name screen-name 
                                          :count 200
                                          :include-rts "false"})))

(defn- get-tweets [screen-name] (->>
                               (get-page screen-name)
                               (map :text)))

(defn send-tweet [text creds]
  (try (statuses-update :oauth-creds creds
                        :params {:status text})
       (catch Exception e (println e)))) 

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
        suffix (if screen-name (str " - @" screen-name) "")]
    (loop [candidate (first candidate-tweets)
           others (rest candidate-tweets)]
      (let [final-tweet (str candidate suffix)]
        (if (< (count final-tweet) 140)
          final-tweet
          (if (empty? others)
            nil
            (recur (first others) (rest others))))))))


(defn create-tweet [[screen-name tweets]]
  (make-valid-tweet screen-name tweets))

(defn reply [new-followers]
  (let [follower  (->> new-followers
                       seq
                       rand-nth)
        the-vec [follower (get-tweets follower)]]
    (let [tweet (create-tweet the-vec)]
      (send-tweet tweet markov-creds))))

(defn store-and-reply-to-followers [handles]
  (reply (find-new-followers handles)))

(defn process-followers [screen-name]
  (->> (followers-list :oauth-creds markov-creds
                       :params {:screen-name screen-name})
       :body
       :users
       (map :screen_name)
       store-and-reply-to-followers
       ))

(defn mock-user [handle]
  (let [tweets (get-tweets handle)
        tweet (create-tweet [handle tweets])]
    (send-tweet tweet markov-creds)))

;; (mock-user "kolemannix")

(defn start-markov-thread [screen-name]
  (loop []
    (Thread/sleep (* 60 15000))
    (process-followers screen-name)
    (recur)))

(defn -main [] (start-markov-thread "mockingmarkov"))
