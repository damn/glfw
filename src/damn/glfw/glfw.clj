(ns damn.glfw.glfw
  "1:1 Clojure names for GLFW 3.3 constants and functions (LWJGL `org.lwjgl.glfw.GLFW`).
  See https://www.glfw.org/docs/3.3/"
  (:import (org.lwjgl.glfw GLFW)))

;; --- constants (window hints, keys, buttons, gamepad) ---

(def true* GLFW/GLFW_TRUE)

(def context-version-major GLFW/GLFW_CONTEXT_VERSION_MAJOR)
(def context-version-minor GLFW/GLFW_CONTEXT_VERSION_MINOR)
(def opengl-profile GLFW/GLFW_OPENGL_PROFILE)
(def opengl-core-profile GLFW/GLFW_OPENGL_CORE_PROFILE)
(def opengl-forward-compat GLFW/GLFW_OPENGL_FORWARD_COMPAT)

(def key-escape GLFW/GLFW_KEY_ESCAPE)
(def key-space GLFW/GLFW_KEY_SPACE)
(def key-enter GLFW/GLFW_KEY_ENTER)
(def key-tab GLFW/GLFW_KEY_TAB)
(def key-backspace GLFW/GLFW_KEY_BACKSPACE)
(def key-delete GLFW/GLFW_KEY_DELETE)
(def key-minus GLFW/GLFW_KEY_MINUS)
(def key-equal GLFW/GLFW_KEY_EQUAL)
(def key-left GLFW/GLFW_KEY_LEFT)
(def key-right GLFW/GLFW_KEY_RIGHT)
(def key-up GLFW/GLFW_KEY_UP)
(def key-down GLFW/GLFW_KEY_DOWN)
(def press GLFW/GLFW_PRESS)
(def release GLFW/GLFW_RELEASE)

(def mouse-button-left GLFW/GLFW_MOUSE_BUTTON_LEFT)
(def mouse-button-right GLFW/GLFW_MOUSE_BUTTON_RIGHT)
(def mouse-button-middle GLFW/GLFW_MOUSE_BUTTON_MIDDLE)

(def joystick-last GLFW/GLFW_JOYSTICK_LAST)

(def gamepad-button-left-bumper GLFW/GLFW_GAMEPAD_BUTTON_LEFT_BUMPER)
(def gamepad-button-right-bumper GLFW/GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER)
(def gamepad-button-a GLFW/GLFW_GAMEPAD_BUTTON_A)
(def gamepad-button-x GLFW/GLFW_GAMEPAD_BUTTON_X)

(def gamepad-axis-left-x GLFW/GLFW_GAMEPAD_AXIS_LEFT_X)
(def gamepad-axis-left-y GLFW/GLFW_GAMEPAD_AXIS_LEFT_Y)

;; --- library lifecycle ---

(defn init! []
  (GLFW/glfwInit))

(defn terminate! []
  (GLFW/glfwTerminate))

(defn poll-events! []
  (GLFW/glfwPollEvents))

(defn swap-interval! [value]
  (GLFW/glfwSwapInterval value))

(defn get-time []
  (GLFW/glfwGetTime))

;; --- window ---

(defn window-hint! [hint value]
  (GLFW/glfwWindowHint hint value))

(defn create-window! [width height title monitor share]
  (GLFW/glfwCreateWindow (int width)
                         (int height)
                         (str title)
                         (long monitor)
                         (long share)))

(defn destroy-window! [window]
  (GLFW/glfwDestroyWindow window))

(defn window-should-close? [window]
  (GLFW/glfwWindowShouldClose window))

(defn set-window-should-close! [window value]
  (GLFW/glfwSetWindowShouldClose window value))

(defn make-context-current! [window]
  (GLFW/glfwMakeContextCurrent window))

(defn swap-buffers! [window]
  (GLFW/glfwSwapBuffers window))

(defn get-framebuffer-size [window w h]
  (GLFW/glfwGetFramebufferSize ^long window w h))

(defn get-window-size [window w h]
  (GLFW/glfwGetWindowSize ^long window w h))

;; --- callbacks ---

(defn set-framebuffer-size-callback! [window f]
  (GLFW/glfwSetFramebufferSizeCallback window f))

(defn set-key-callback! [window f]
  (GLFW/glfwSetKeyCallback window f))

(defn set-cursor-pos-callback! [window f]
  (GLFW/glfwSetCursorPosCallback window f))

(defn set-mouse-button-callback! [window f]
  (GLFW/glfwSetMouseButtonCallback window f))

;; --- input ---

(defn get-key [window key]
  (GLFW/glfwGetKey window (int key)))

(defn get-cursor-pos [window x y]
  (GLFW/glfwGetCursorPos window x y))

(defn get-mouse-button [window button]
  (GLFW/glfwGetMouseButton window button))

;; --- cursor ---

(defn set-cursor! [window cursor]
  (GLFW/glfwSetCursor window cursor))

(defn destroy-cursor! [cursor]
  (GLFW/glfwDestroyCursor cursor))

(defn create-cursor! [image hotspot-x hotspot-y]
  (GLFW/glfwCreateCursor image hotspot-x hotspot-y))

;; --- joystick / gamepad ---

(defn joystick-present? [jid]
  (GLFW/glfwJoystickPresent jid))

(defn joystick-is-gamepad? [jid]
  (GLFW/glfwJoystickIsGamepad jid))

(defn get-gamepad-state [jid state]
  (GLFW/glfwGetGamepadState jid state))

(defn get-joystick-buttons [jid]
  (GLFW/glfwGetJoystickButtons jid))

(defn get-joystick-axes [jid]
  (GLFW/glfwGetJoystickAxes jid))
