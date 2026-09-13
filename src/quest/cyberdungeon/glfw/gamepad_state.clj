(ns quest.cyberdungeon.glfw.gamepad-state
  "LWJGL `GLFWGamepadState` stack allocation."
  (:import (org.lwjgl.glfw GLFWGamepadState)))

(defn calloc []
  (GLFWGamepadState/calloc))

(defn free! [^GLFWGamepadState state]
  (.free state))

(defn button [^GLFWGamepadState state index]
  (.buttons state (int index)))

(defn axis [^GLFWGamepadState state index]
  (.axes state (int index)))
