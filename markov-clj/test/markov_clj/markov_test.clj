(ns markov-clj.markov-test
  (:require [midje.sweet :refer :all]
            ;; [midje.util :refer [testable-privates]]
            [markov-clj.markov :refer :all]))


(def corpus (re-seq #"\w+" (slurp "resources/corpus.txt")))

(def baby-corpus (re-seq #"\w+" (slurp "resources/corpus2.txt")))

(println baby-corpus)
(build-database baby-corpus 2)

(fact "Database is generated properly for small corpus"
  (build-database baby-corpus 3) => {["off" "The"] {"car" 1},
                                     ["jumped" "off"] {"The" 1},
                                     ["The" "car"] {"is" 1},
                                     ["kid" "jumped"] {"off" 1},
                                     ["sky" "is"] {"foggy" 1, "sunny" 1, "cloudy" 1, "blue" 1},
                                     ["is" "sunny"] {"The" 1},
                                     ["cloudy" "The"] {"sky" 1},
                                     ["is" "blue"] {"The" 1},
                                     ["The" "sky"] {"is" 4},
                                     ["is" "fast"] {"The" 1},
                                     ["The" "kid"] {"jumped" 1},
                                     ["fast" "The"] {"sky" 1},
                                     ["car" "is"] {"fast" 1},
                                     ["blue" "The"] {"kid" 1},
                                     ["is" "cloudy"] {"The" 1},
                                     ["sunny" "The"] {"sky" 1}}
  (build-database baby-corpus 2) => {["sky"] {"is" 4},
                                     ["cloudy"] {"The" 1},
                                     ["kid"] {"jumped" 1},
                                     ["is"] {"foggy" 1, "sunny" 1, "cloudy" 1, "fast" 1, "blue" 1},
                                     ["jumped"] {"off" 1},
                                     ["off"] {"The" 1},
                                     ["The"] {"car" 1, "kid" 1, "sky" 4},
                                     ["car"] {"is" 1},
                                     ["sunny"] {"The" 1},
                                     ["blue"] {"The" 1},
                                     ["fast"] {"The" 1}}
  )
;; The above data generated using clojure.pprint/pprint

(lookup ["The"] (build-database baby-corpus 2))

(create-sentence "The car" (build-database baby-corpus 3))
