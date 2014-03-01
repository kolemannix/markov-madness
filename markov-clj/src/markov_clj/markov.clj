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
                                     (generate-tuples n ,,)
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

(defn next-word [database {prefix :prefix word :word}]
  (println "Next word with \n" prefix "\n" word)
  (let [value-map (database prefix)
        word (select-randomly value-map)
        _ (println word)
        __ (println prefix)
        new-prefix (conj (subvec prefix 1) word)]
    {:word word :prefix new-prefix}))

(defn create-sentence [database seed]
  (iterate #(next-word database %) {:prefix seed}))

