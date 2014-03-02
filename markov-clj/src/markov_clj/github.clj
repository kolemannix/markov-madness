(ns markov-clj.github
  (:require
   [tentacles
    [repos :as repos]
    [users :as users]]
   [markov-clj.markov :as markov]))

(def token "3c08f623cbc8df474c220f3970383897336fc072")

(defn get-repo-names [user]
  (map :name (repos/user-repos user {:oauth-token token :per-page 100})))

(defn get-commits [username repo-name]
  (repos/commits username repo-name {:oauth-token token :per-page 100}))

(defn extract-repo-commit-messages [username repo]
  (->> repo
   (map (fn [c]
          (println (c :author))
          (if
              (and (contains? c :commit) (contains? (c :author) :login) (= username ((c :author) :login)))
            ((c :commit) :message))))
   stringify))

(defn get-commits-and-extract-messages [user repo]
  (->> (get-commits user repo)
       (extract-repo-commit-messages user )))

(defn get-user-commit-corpus [user]
  (let [repo-names (remove nil? (get-repo-names user))]
    (->> repo-names
     (map #(get-commits-and-extract-messages user %))
     stringify)))

(defn- stringify [coll]
  (apply str (interpose " " coll)))

(spit "resources/brent_commits.txt" (get-user-commit-corpus "bbaumgar"))

(take 3 (markov/generate (slurp "resources/brent_commits.txt") 1))
