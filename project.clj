(defproject visibility-2d "0.1.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.521"]]
  :plugins [[lein-cljsbuild "1.1.6"]
            [lein-figwheel "0.5.10"]]
  :clean-targets ^{:protect false} ["target"
                                    "resources/public/js"
                                    ".lein-repl-history"
                                    "figwheel_server.log"]
  :cljsbuild {:builds
              [{:id "debug"
                :source-paths ["src"]
                :figwheel true
                :compiler {:output-to "resources/public/js/visibility-2d.js"
                           :output-dir "resources/public/js/out"
                           :optimizations :none
                           :source-map true}}
               {:id "release"
                :source-paths ["src"]
                :compiler {:elide-asserts true
                           :pretty-print false
                           :output-to "resources/public/js/visibility-2d.min.js"
                           :optimizations :advanced}}]}
  :figwheel {:css-dirs ["resources/public/css"]})
