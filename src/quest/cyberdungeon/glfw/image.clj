(ns quest.cyberdungeon.glfw.image
  "Build a cursor image for [[quest.cyberdungeon.glfw.glfw/create-cursor!]].

  Typical flow: [[malloc]] → [[width!]] / [[height!]] / [[pixels!]] → pass to
  [[quest.cyberdungeon.glfw.glfw/create-cursor!]] → [[free!]] after
  [[quest.cyberdungeon.glfw.glfw/destroy-cursor!]]."
  (:import (org.lwjgl.glfw GLFWImage)
           (java.nio ByteBuffer)))

(defn malloc
  "Allocate a stack `GLFWImage` descriptor. Populate before [[quest.cyberdungeon.glfw.glfw/create-cursor!]]."
  []
  (GLFWImage/malloc))

(defn free!
  "Release memory for `image` from [[malloc]] after the cursor is destroyed."
  [^GLFWImage image]
  (.free image))

(defn width!
  "Set image width in pixels on `image`."
  [^GLFWImage image w]
  (.width image (int w)))

(defn height!
  "Set image height in pixels on `image`."
  [^GLFWImage image h]
  (.height image (int h)))

(defn pixels!
  "Set RGBA pixel data on `image` (direct `ByteBuffer`, width × height × 4 bytes)."
  [^GLFWImage image ^ByteBuffer pixels]
  (.pixels image pixels))
