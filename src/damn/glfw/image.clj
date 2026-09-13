(ns damn.glfw.image
  "LWJGL `GLFWImage` helpers for custom cursors."
  (:import (org.lwjgl.glfw GLFWImage)
           (java.nio ByteBuffer)))

(defn malloc []
  (GLFWImage/malloc))

(defn free! [^GLFWImage image]
  (.free image))

(defn width! [^GLFWImage image w]
  (.width image (int w)))

(defn height! [^GLFWImage image h]
  (.height image (int h)))

(defn pixels! [^GLFWImage image ^ByteBuffer pixels]
  (.pixels image pixels))
