(defproject pending-reboot-handler "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
;  :dev-dependencies [[lein-git-deps "0.0.1-SNAPSHOT"]]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-time "0.12.0"]
                 [clarango "0.7.1"]]
;  :git-dependencies [["https://github.com/boorad/clarango.git"]]
  :main ^:skip-aot pending-reboot-handler.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
