(ns markov-clj.twitter
  (:use
   [twitter.oauth]
   [twitter.callbacks]
   [twitter.callbacks.handlers]
   [twitter.api.restful])
  (:import
   (twitter.callbacks.protocols SyncSingleCallback)))


(def my-creds (apply make-oauth-creds
                     (clojure.string/split-lines
                      (slurp "twitter_auth.txt"))))

; simply retrieves the user, authenticating with the above credentials
; note that anything in the :params map gets the -'s converted to _'s
(defn- get-statuses [user-id] (->>
                               (:body (statuses-user-timeline :oauth-creds my-creds
                                                              :params {:screen-name user-id}))
                               (map :text)))

(get-statuses "bbombgardener")


; shows the users friends
(friendships-show :oauth-creds my-creds 
                  :params {:target-screen-name "AdamJWynne"})

; use a custom callback function that only returns the body of the response
(friendships-show :oauth-creds my-creds
                  :callbacks (SyncSingleCallback. response-return-body 
                                                  response-throw-error
                                                  exception-rethrow)      
                  :params {:target-screen-name "AdamJWynne"})

; upload a picture tweet with a text status attached, using the default sync-single callback
(statuses-update-with-media :oauth-creds *creds*
                            :body [(file-body-part "/pics/test.jpg")
                                   (status-body-part "testing")])
