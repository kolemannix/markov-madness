(ns markov-clj.twitter
  (:gen-class)
  (:use
   [twitter.oauth]
   [twitter.callbacks]
   [twitter.callbacks.handlers]
   [twitter.api.restful]
   [twitter.request]
   [twitter.api.streaming]
   [clojure.data.json :as json ])
  (:require
   [http.async.client :as ac])
  (:import
   (twitter.callbacks.protocols SyncSingleCallback)
   (twitter.callbacks.protocols AsyncStreamingCallback)))


(def my-creds (apply make-oauth-creds
                     (clojure.string/split-lines
                      (slurp "twitter_auth.txt"))))

; simply retrieves the user, authenticating with the above credentials
; note that anything in the :params map gets the -'s converted to _'s

(defn- get-page [user-id & since-id]
  (:body (statuses-user-timeline :oauth-creds my-creds
                                 :params {:user-id user-id
                                          :count 200
                                          :include-rts "false"})))

(defn- get-tweets [user-id] (->>
                               (get-page user-id)
                               (map :text)))

(defn tweet-the-twitter [text]
  (statuses-update :oauth-creds my-creds
                   :params {:status text})) 

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

(reply (find-new-followers [61524108 752422021]))

(defn reply [new-followers]
  (->>
   (map #(vector % (get-tweets %)) new-followers)
   (map markov-that-thun-thun-thun)))

(defn store-and-reply-to-followers [ids]
  (println ids)
  (reply (find-new-followers ids)))

(defn markov-that-thun-thun-thun [[id tweets]]
  (println id tweets))

(defn process-followers []
  (->> (followers-ids :oauth-creds my-creds
                      :params {:screen-name "mockingmarkov"})
       :body
       :ids
       store-and-reply-to-followers
       ))

(process-followers)

(defn -main [] (start-filtering))
