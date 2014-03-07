(defproject pail-schema "0.1.1"
  :description "Library for using Prismatic Schema, Pail and Cascalog."
  :url "http://github.com/EricGebhart/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.0.0"

  :source-paths ["src"]

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.backtype/dfs-datastores "1.3.4"]
                 [org.apache.hadoop/hadoop-core "1.2.0" ]
                 [potemkin "0.3.4"]
                 [cascalog "2.0.0" ]
                 [clj-pail-tap "0.1.1"]
                 [pail-fressian "0.1.2"]
                 [prismatic/schema "0.2.0"]
                 [pail-cascalog "0.1.0"]]

  :aot [pail-schema.data-unit-pail-structure]

  :profiles {:1.3 {:dependencies [[org.clojure/clojure "1.3.0"]]}
             :1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}
             :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}
             :1.6 {:dependencies [[org.clojure/clojure "1.6.0-master-SNAPSHOT"]]}

             :dev {:dependencies [[midje "1.5.1"]]
                   :plugins [[lein-midje "3.0.1"]]}}


  :deploy-repositories [["releases" {:url "https://clojars.org/repo" :username :gpg :password :gpg}]
                        ["snapshots" {:url "https://clojars.org/repo" :username :gpg :password :gpg}]])
