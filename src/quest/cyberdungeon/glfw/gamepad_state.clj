(ns quest.cyberdungeon.glfw.gamepad-state
  "Scratch buffer for [[quest.cyberdungeon.glfw.glfw/get-gamepad-state]].

  Pair with [[calloc]] / [[free!]] around reads; use [[button]] and [[axis]] with constants
  from [[quest.cyberdungeon.glfw.glfw]] such as [[quest.cyberdungeon.glfw.glfw/gamepad-button-a]]."
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
  "Button value at `index` (e.g. [[quest.cyberdungeon.glfw.glfw/gamepad-button-a]]) after a
  successful [[quest.cyberdungeon.glfw.glfw/get-gamepad-state]]. Compare to
  [[quest.cyberdungeon.glfw.glfw/press]]."
  [^GLFWGamepadState state index]
  (.buttons state (int index)))

(defn axis
  "Axis value at `index` (e.g. [[quest.cyberdungeon.glfw.glfw/gamepad-axis-left-x]]) in
  `-1..1` after [[quest.cyberdungeon.glfw.glfw/get-gamepad-state]]."
  [^GLFWGamepadState state index]
  (.axes state (int index)))
