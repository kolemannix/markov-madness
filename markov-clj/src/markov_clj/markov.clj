(ns markov-clj.markov)

(defn generate-tuples [n corpus]
  (partition n 1 corpus))

(defn index-tuple [tuple]
  (let [key (vec (drop-last tuple))
        value (last tuple)]
    [key value]))

(defn index-frequencies [database [key val]]
  (if-let [val-at-key (database key)]
    ;; Prefix is in database
    (let [new-val-at-key (if (contains? val-at-key val)
                           (assoc val-at-key val (inc (val-at-key val)))
                           (assoc val-at-key val 1))]
      (assoc database key new-val-at-key))
    ;; Prefix is not in database
    (assoc database key {val 1})))

(defn build-database [corpus n] (->> corpus
                                     (generate-tuples (inc n) ,,)
                                     (map index-tuple ,,)
                                     (reduce index-frequencies {} ,,)))


;; TODO map-filtered somethingwhatsit

(defn lookup [key database]
  (database key))

(defn select-randomly [val-map]
  (->> val-map
       (map (fn [[k v]] (repeat v k)))
       flatten
       rand-nth))

(defn next-word [database {prefix :prefix}]
  (if-let [value-map (database prefix)]
    (let [new-word (select-randomly value-map)
          new-prefix (conj (subvec prefix 1) new-word)]
      {:word new-word :prefix new-prefix})
    {:word :tail}))

(defn choose-seed [database]
  (key (let [capital-seeds (filter (fn [[k v]]
                                 (Character/isUpperCase (ffirst k))) database)]
     (println capital-seeds)
     (if (not (empty? capital-seeds))
       (rand-nth (seq capital-seeds))
       (rand-nth (seq database))))))

(defn generate [corpus]
  (let [database (build-database 2 corpus)]
    ()))

(defn create-sentence [database seed]
  (loop [{word :word :as prefix-map} {:word :head :prefix seed} words []]
    (if (= :tail word)
      words
      (recur (next-word database prefix-map) (conj words word)))))
