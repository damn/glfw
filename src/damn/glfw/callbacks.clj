(ns damn.glfw.callbacks
  "Free GLFW window callbacks registered via `damn.glfw.glfw` setters."
  (:import (org.lwjgl.glfw Callbacks)))

(defn free!
  "Call `glfwFreeCallbacks` for `window`."
  [window]
  (Callbacks/glfwFreeCallbacks window))
