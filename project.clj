(defproject sauerworld/cube2.crypto "1.1.0"
  :description "clojure implementation of Cube 2: Sauerbraten authentication crypto library"
  :url "https://github.com/mgaare/cube2-crypto"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:dev
             {:source-paths ["dev"]
              :dependencies [[org.clojars.jcrossley3/tools.namespace "0.2.4.1"]
                             [criterium "0.4.6"]]}}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.bouncycastle/bcprov-jdk15on "1.58"]
                 [crypto-random "1.1.0"]])
