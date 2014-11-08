(defproject visibility-2d "0.1.0"
  :description "2D Visibility"
  :url ""

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-3196"]]

  :plugins [[lein-cljsbuild "1.0.5"]]

  :clean-targets ["target"]

  :hooks [leiningen.cljsbuild]

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src-cljs"]
                        :compiler {:pretty-print true
                                   :output-to "resources/public/js/visibility-2d.js"
                                   :optimizations :whitespace}}
                       {:id "release"
                        :source-paths ["src-cljs"]
                        :compiler {:elide-asserts true
                                   :pretty-print false
                                   :output-to "resources/public/js/visibility-2d.min.js"
                                   :optimizations :advanced}}]})
