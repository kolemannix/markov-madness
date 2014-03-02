(ns markov-clj.markov-test
  (:require [midje.sweet :refer :all]
            ;; [midje.util :refer [testable-privates]]
            [markov-clj.markov :refer :all]))


(def corpus (clojure.string/split (slurp "resources/corpus.txt") #"\s+"))
(println corpus)

(def baby-corpus (clojure.string/split (slurp "resources/corpus2.txt") #"\s+"))

(time (count (build-database corpus 3)))
     ;.;. Actual: {["The"] {"car" 1, "kid" 1, "sky" 4}, ["blue."] {"The" 1}, ["car"] {"is" 1}, ["cloudy."] {"The" 1}, ["fast."] {"The" 1}, ["is"] {"blue." 1, "cloudy." 1, "fast." 1, "foggy." 1, "sunny." 1}, ["jumped"] {"off." 1}, ["kid"] {"jumped" 1}, ["off."] {"The" 1}, ["sky"] {"is" 4}, ["sunny."] {"The" 1}}
   ;.;. Expected: {["The"] {"car" 1, "kid" 1, "sky" 4}, ["blue."] {"The" 1}, ["car"] {"is" 1}, ["cloudy."] {"The" 1}, ["fast."] {"The" 1}, ["is"] {"blue." 1, "cloudy." 1, "fast." 1, "foggy." 1, "sunny." 1}, ["jumped"] {"off" 1}, ["kid"] {"jumped" 1}, ["off."] {"The" 1}, ["sky"] {"is" 4}, ["sunny."] {"The" 1}}
;.;. FAIL "Database is generated properly for small corpus" at (form-init166712679504669725.clj:18)
(fact "Database is generated properly for small corpus"
  (build-database baby-corpus 2) => {["off." "The"] {"car" 1},
                                     ["jumped" "off."] {"The" 1},
                                     ["The" "car"] {"is" 1},
                                     ["kid" "jumped"] {"off." 1},
                                     ["sky" "is"] {"foggy." 1, "sunny." 1, "cloudy." 1, "blue." 1},
                                     ["is" "sunny."] {"The" 1},
                                     ["cloudy." "The"] {"sky" 1},
                                     ["is" "blue."] {"The" 1},
                                     ["The" "sky"] {"is" 4},
                                     ["is" "fast."] {"The" 1},
                                     ["The" "kid"] {"jumped" 1},
                                     ["fast." "The"] {"sky" 1},
                                     ["car" "is"] {"fast." 1},
                                     ["blue." "The"] {"kid" 1},
                                     ["is" "cloudy."] {"The" 1},
                                     ["sunny." "The"] {"sky" 1}}
  (build-database baby-corpus 1) => {["sky"] {"is" 4},
                                     ["cloudy."] {"The" 1},
                                     ["kid"] {"jumped" 1},
                                     ["is"] {"foggy." 1, "sunny." 1, "cloudy." 1, "fast." 1, "blue." 1},
                                     ["jumped"] {"off." 1},
                                     ["off."] {"The" 1},
                                     ["The"] {"car" 1, "kid" 1, "sky" 4},
                                     ["car"] {"is" 1},
                                     ["sunny."] {"The" 1},
                                     ["blue."] {"The" 1},
                                     ["fast."] {"The" 1}}
  )

;; The above data generated using clojure.pprint/pprint

(fact "lookup function behaves properly"
  (lookup ["The"] (build-database baby-corpus 1)) => {"car" 1, "kid" 1, "sky" 4})

(fact "select randomly function behaves properly"
  (select-randomly {"foggy" 1, "sunny" 5, "cloudy" 1, "blue" 1}) => (some-checker
                                                                     "foggy" "sunny" "cloudy" "blue"))

(fact "Capital seeds are chosen when available"
  (key (choose-seed (build-database baby-corpus 2))) => (some-checker ["The" "kid"]
                                                                  ["The" "car"]
                                                                  ["The" "sky"]))
(def db (build-database baby-corpus 2))
(create-sentence db)
