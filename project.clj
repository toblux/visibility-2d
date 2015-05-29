(defproject visibility-2d "0.1.0"
  :description "2D Visibility"
  :url "https://toblux.github.io/visibility-2d/"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-3211"]]

  :plugins [[lein-cljsbuild "1.0.6"]]

  :clean-targets ^{:protect false} ["target" "resources/public/js"]

  :hooks [leiningen.cljsbuild]

  :cljsbuild {:builds [{:id "debug"
                        :source-paths ["src-cljs"]
                        :compiler {:output-to "resources/public/js/visibility-2d.js"
                                   :output-dir "resources/public/js/out"
                                   :optimizations :none
                                   :source-map true}}
                       {:id "release"
                        :source-paths ["src-cljs"]
                        :compiler {:elide-asserts true
                                   :pretty-print false
                                   :output-to "resources/public/js/visibility-2d.min.js"
                                   :optimizations :advanced}}]})
