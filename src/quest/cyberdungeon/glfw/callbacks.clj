(ns quest.cyberdungeon.glfw.callbacks
  "Release GLFW per-window callback slots after using the `set-*-callback!` functions in
  [[quest.cyberdungeon.glfw.glfw]]."
  (:import (org.lwjgl.glfw Callbacks)))

(defn free!
  "Free all callbacks on `window` registered through
  [[quest.cyberdungeon.glfw.glfw/set-framebuffer-size-callback!]],
  [[quest.cyberdungeon.glfw.glfw/set-key-callback!]],
  [[quest.cyberdungeon.glfw.glfw/set-cursor-pos-callback!]], and
  [[quest.cyberdungeon.glfw.glfw/set-mouse-button-callback!]].

  Call in a `finally` block before [[quest.cyberdungeon.glfw.glfw/destroy-window!]]."
  [window]
  (Callbacks/glfwFreeCallbacks window))
