(defproject noelbot "0.0.2-SNAPSHOT"
  :description "IRC bot successing Chaikabot"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.typed "0.3.0-alpha2"]
                 [environ "1.0.0"]
                 [irclj "0.5.0-alpha4"]
                 [little-couch "0.1.1"]]
  :plugins [[lein-typed "0.3.5"]
            [lein-environ "1.0.0"]]
  :main ^:skip-aot noelbot.core
  :target-path "target/%s"
  :profiles {:uberjar    {:aot :all
                          :env {:conf "prod.edn"}}
             :dev        {:env {:conf "dev.edn"}}})
