(ns markov-clj.markov)

(def corpus
  (re-seq #"\w+" (slurp "corpus.txt")))

(def baby-corpus (re-seq #"\w+" (slurp "corpus2.txt")))

(defn- generate-tuples [n corpus]
  (partition n 1 corpus))

(println (generate-tuples 3 corpus))

(defn index-tuple [tuple]
  (let [key (vec (drop-last tuple))
        value (last tuple)]
    [key value]))

(defn build-database [corpus n] (->> corpus
                                     (generate-tuples n ,,)
                                     (map index-tuple ,,)
                                     (into {}) ,,))


(->> corpus
     (generate-tuples 2 ,,)
     (map index-tuple ,,)
     (reduce index-frequencies ,,))

(defn- index-frequencies [database [key val]]
  (if-let [val-at-key (database key)]
    ;; Prefix is in database
    (if (contains? val-at-key val)
      (assoc val-at-key val (inc (val-at-key val)))
      (assoc val-at-key val 1))
    ;; Prefix is not in database
    (assoc database key {val 1})))

(println (build-database corpus 2))
;; TODO map-filtered somethingwhatsit
(defn- create-sentence [seed database]
  (letfn (iterate-word [sentence]
           (let [next-key (take-last 2 sentence)]
             )))
  (vals (filter (fn [[k v]] (= k seed)) database)))

(defn- lookup [key database]
  (vals (filter (fn [[k v]] (= k key)) database)))
(lookup ["The"] (build-database baby-corpus 2))

(println (build-database baby-corpus 3))
(println (create-sentence "The car" (build-database baby-corpus 3)))
