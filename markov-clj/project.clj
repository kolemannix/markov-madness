(defproject markov-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :uberjar-name "markov-madness.jar"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [midje "1.6.2"]
                 [twitter-api "0.7.5"]
                 [tentacles "0.2.5"]
                 [ring/ring-json "0.2.0"]
                 [http-kit "2.0.0"]
                 [ring/ring-devel "1.1.8"]
                 [compojure "1.1.5"]
                 [ring-cors "0.1.0"
                  ]]
  :main markov-clj.server)
