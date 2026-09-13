(ns quest.cyberdungeon.glfw.window-test
  "Opens a window, polls events until closed. Run: `lein window-test`."
  (:require [quest.cyberdungeon.glfw.glfw :as glfw])
  (:gen-class))

(defn -main
  [& _]
  (when-not (glfw/init!)
    (throw (ex-info "Unable to initialize GLFW" {})))
  (try
    (glfw/window-hint! glfw/context-version-major 3)
    (glfw/window-hint! glfw/context-version-minor 2)
    (glfw/window-hint! glfw/opengl-profile glfw/opengl-core-profile)
    (glfw/window-hint! glfw/opengl-forward-compat glfw/true*)
    (let [window (glfw/create-window! 640 480 "quest.cyberdungeon.glfw window test" 0 0)]
      (when (zero? window)
        (throw (ex-info "Couldn't create window" {})))
      (try
        (glfw/make-context-current! window)
        (glfw/swap-interval! 1)
        (loop []
          (when-not (glfw/window-should-close? window)
            (glfw/poll-events!)
            (glfw/swap-buffers! window)
            (recur)))
        (finally
          (glfw/destroy-window! window))))
    (finally
      (glfw/terminate!))))
