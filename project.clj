(defproject com.github.damn/glfw "0.1.0-SNAPSHOT"
  :description "Clojure facade over LWJGL GLFW 3.3"
  :url "https://github.com/damn/glfw"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.0"]
                   [org.lwjgl/lwjgl-glfw "3.3.3"]]
  :plugins [[lein-codox "0.10.8"]]
  :source-paths ["src"]
  :test-paths ["test"]
  :codox {:name "quest.cyberdungeon.glfw"
          :description "Clojure wrapper for LWJGL GLFW."
          :output-path "target/doc"
          :source-paths ["src"]
          :metadata {:doc/format :markdown}}
  :global-vars {*warn-on-reflection* true}
  :jvm-opts ~(into ["-Dorg.lwjgl.system.allocator=system"]
                   (when (re-find #"(?i)mac" (System/getProperty "os.name" ""))
                     ["-XstartOnFirstThread"]))
  :profiles {:dev {:dependencies [[org.lwjgl/lwjgl "3.3.3" :classifier "natives-macos"]
                                  [org.lwjgl/lwjgl-glfw "3.3.3" :classifier "natives-macos"]]}
             :test {:dependencies [[org.lwjgl/lwjgl "3.3.3" :classifier "natives-macos"]
                                   [org.lwjgl/lwjgl-glfw "3.3.3" :classifier "natives-macos"]]}
             ;; Codox loads ns forms (evals GLFW constants); needs natives on the host OS.
             :codox {:dependencies [[org.lwjgl/lwjgl "3.3.3" :classifier "natives-linux"]
                                    [org.lwjgl/lwjgl-glfw "3.3.3" :classifier "natives-linux"]]}
             ;; `lein run` only sees :source-paths; add test for the smoke-test alias.
             :run-test {:source-paths ["src" "test"]}}
  :aliases {"window-test" ["with-profile" "+dev,+run-test" "run" "-m" "quest.cyberdungeon.glfw.window-test"]})
