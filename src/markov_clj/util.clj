(ns markov-clj.util)

(defn tick
  "Call f with args every ms. First call will be after ms"
  [ms f & args]

  (future
    (doseq [f (repeatedly #(apply f args))]
      (Thread/sleep ms)
      (f))))

(defn tick-now
  "Call f with args every ms. First call will be immediately (and blocking)"
  [ms f & args]

  (apply f args)
  (apply tick ms f args))

(defn periodicly [fun time] 
  "starts a thread that calls function every time ms" 
  (let [thread (new Thread (fn [] (loop [] (fun) (Thread/sleep time) 
                                        (recur))))] 
    (.start thread) 
    thread)) 
