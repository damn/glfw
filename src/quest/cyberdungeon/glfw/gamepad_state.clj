(ns quest.cyberdungeon.glfw.gamepad-state
  "Scratch buffer for [[quest.cyberdungeon.glfw.glfw/get-gamepad-state]].

  Pair with [[calloc]] / [[free!]] around reads; use [[button]] and [[axis]] with keywords
  (e.g. `:a`, `:left-bumper`, `:left-x`)."
  (:require [quest.cyberdungeon.glfw.glfw :as glfw])
  (:import (org.lwjgl.glfw GLFWGamepadState)))

(defn calloc
  "Allocate a stack gamepad state object for [[quest.cyberdungeon.glfw.glfw/get-gamepad-state]]."
  []
  (GLFWGamepadState/calloc))

(defn free!
  "Release `state` from [[calloc]]."
  [^GLFWGamepadState state]
  (.free state))

(defn button
  "Button at keyword `button` (e.g. `:a`) after [[quest.cyberdungeon.glfw.glfw/get-gamepad-state]].
  Compare to `:press` / `:release` via [[quest.cyberdungeon.glfw.glfw/action-keyword]]."
  [^GLFWGamepadState state button]
  (.buttons state (int (glfw/gamepad-button->glfw button))))

(defn axis
  "Axis at keyword `axis` (e.g. `:left-x`) in `-1..1` after [[quest.cyberdungeon.glfw.glfw/get-gamepad-state]]."
  [^GLFWGamepadState state axis]
  (.axes state (int (glfw/gamepad-axis->glfw axis))))
