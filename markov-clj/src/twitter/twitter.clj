(ns markov-clj.twitter
  (:use
   [twitter.oauth]
   [twitter.callbacks]
   [twitter.callbacks.handlers]
   [twitter.api.restful]
   [twitter.request])
  (:import
   (twitter.callbacks.protocols SyncSingleCallback)))


(def my-creds (apply make-oauth-creds
                     (clojure.string/split-lines
                      (slurp "twitter_auth.txt"))))

; simply retrieves the user, authenticating with the above credentials
; note that anything in the :params map gets the -'s converted to _'s
(defn- get-tweets [user-id] (->>
                               (get-page user-id)
                               (map :text)))

(defn- get-page [user-id & since-id]
  (:body (statuses-user-timeline :oauth-creds my-creds
                                 :params {:screen-name user-id
                                          :count 200
                                          :include-rts "false"})))

(get-tweets "bbombgardener")

(statuses-update :oauth-creds my-creds
                 :params {:status "Hello, world!"}) 
