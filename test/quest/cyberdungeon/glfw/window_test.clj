(ns quest.cyberdungeon.glfw.window-test
  "Smoke test: open a window and poll until closed. Run: `lein window-test`."
  (:require [quest.cyberdungeon.glfw.glfw :as glfw]))

(defn -main
  [& _]
  (glfw/with-state
    (glfw/window-hints! {:context-version-major 3
                         :context-version-minor 2
                         :opengl-profile :opengl-core-profile
                         :opengl-forward-compat true})
    (glfw/with-window [window {:width 640 :height 480 :title "quest.cyberdungeon.glfw window test"
                               :monitor 0 :share 0}]
      (glfw/make-context-current! window)
      (glfw/swap-interval! 1)
      (loop []
        (when-not (glfw/window-should-close? window)
          (glfw/poll-events!)
          (glfw/swap-buffers! window)
          (recur))))))
