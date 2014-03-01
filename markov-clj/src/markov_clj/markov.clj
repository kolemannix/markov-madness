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

(defn next-word [prefix database]
  (let [value-map (database prefix)]
    (select-randomly value-map)))

(defn select-randomly [val-map]
  (-> val-map
      (map (fn [[k v]] (repeat v k)) ,,)
      (apply conj ,,)
      flatten
      rand-nth))

(select-randomly {"foggy" 1, "sunny" 5, "cloudy" 1, "blue" 1})

(defn create-sentence [seed database]
  (iterate #(next-word % database) seed))

(defn lookup [key database]
  (database key))
