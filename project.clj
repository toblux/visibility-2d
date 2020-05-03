(defproject visibility-2d "0.1.0"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.753"]]

  :plugins [[lein-cljsbuild "1.1.8"]
            [lein-figwheel "0.5.19"]]

  :clean-targets ^{:protect false} ["target"
                                    "resources/public/js"
                                    ".rebel_readline_history"
                                    ".lein-repl-history"
                                    "figwheel_server.log"]

  :cljsbuild {:builds [{:id           "debug"
                        :figwheel     true
                        :source-paths ["src"]
                        :compiler     {:main          "visibility-2d.core"
                                       :asset-path    "js"
                                       :output-to     "resources/public/js/visibility-2d.js"
                                       :output-dir    "resources/public/js"
                                       :optimizations :none
                                       :source-map    true}}
                       {:id           "release"
                        :source-paths ["src"]
                        :compiler     {:elide-asserts true
                                       :pretty-print  false
                                       :output-to     "resources/public/js/visibility-2d.js"
                                       :optimizations :advanced}}]}

  :figwheel {:css-dirs ["resources/public/css"]})
