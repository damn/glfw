(ns quest.cyberdungeon.glfw.glfw
  "Clojure facade for GLFW 3.3 windowing and input.

  Typical lifecycle: [[with-state]] → [[window-hints!]] (optional) → [[with-window]] →
  [[make-context-current!]] → loop of [[poll-events!]] and [[swap-buffers!]].

  Register input with the `set-*-callback!` functions; release native callback slots with
  [[quest.cyberdungeon.glfw.callbacks/free!]] when done.

  Portable JAR only — applications must add `lwjgl-glfw` (and usually `lwjgl`) **native**
  classifiers for their OS.

  **Load-time natives:** top-level `def`s read `GLFW/…` static fields when this namespace
  is loaded (`require`, REPL, Codox, etc.). That initializes LWJGL and loads JNI for the
  current platform. Tools that only need docstrings (e.g. Codox on CI) must still put the
  matching native JARs on the classpath."
  (:require [clojure.string :as str])
  (:import (org.lwjgl.glfw GLFW)))

;; --- input keywords ---

(def ^:private key->gl
  (merge
   {:escape GLFW/GLFW_KEY_ESCAPE
    :space GLFW/GLFW_KEY_SPACE
    :enter GLFW/GLFW_KEY_ENTER
    :tab GLFW/GLFW_KEY_TAB
    :backspace GLFW/GLFW_KEY_BACKSPACE
    :delete GLFW/GLFW_KEY_DELETE
    :minus GLFW/GLFW_KEY_MINUS
    :equal GLFW/GLFW_KEY_EQUAL
    :left GLFW/GLFW_KEY_LEFT
    :right GLFW/GLFW_KEY_RIGHT
    :up GLFW/GLFW_KEY_UP
    :down GLFW/GLFW_KEY_DOWN}
   (into {}
         (for [c "ABCDEFGHIJKLMNOPQRSTUVWXYZ"]
           [(keyword (str/lower-case (str c))) (int c)]))
   (into {}
         (for [c "0123456789"]
           [(keyword (str c)) (int c)]))))

(def ^:private mouse-button->gl
  {:mouse-left GLFW/GLFW_MOUSE_BUTTON_LEFT
   :mouse-right GLFW/GLFW_MOUSE_BUTTON_RIGHT
   :mouse-middle GLFW/GLFW_MOUSE_BUTTON_MIDDLE})

(def ^:private gamepad-button->gl
  {:left-bumper GLFW/GLFW_GAMEPAD_BUTTON_LEFT_BUMPER
   :right-bumper GLFW/GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER
   :a GLFW/GLFW_GAMEPAD_BUTTON_A
   :x GLFW/GLFW_GAMEPAD_BUTTON_X})

(def ^:private gamepad-axis->gl
  {:left-x GLFW/GLFW_GAMEPAD_AXIS_LEFT_X
   :left-y GLFW/GLFW_GAMEPAD_AXIS_LEFT_Y})

(def ^:private gl->action
  {GLFW/GLFW_PRESS :press
   GLFW/GLFW_RELEASE :release})

(def ^:private gl->key
  (into {} (map (fn [[kw code]] [code kw]) key->gl)))

(def ^:private gl->mouse-button
  (into {} (map (fn [[kw code]] [code kw]) mouse-button->gl)))

(defn- key-code->keyword
  [code]
  (or (get gl->key (int code)) (int code)))

(defn- mouse-button-code->keyword
  [code]
  (or (get gl->mouse-button (int code))
      (throw (ex-info "Unknown mouse button code" {:code code}))))

(defn- key->glfw
  [key]
  (cond
    (keyword? key) (or (key->gl key)
                         (throw (ex-info "Unknown key" {:key key})))
    (integer? key) (int key)
    :else (throw (ex-info "Key must be keyword or int" {:key key}))))

(defn- mouse-button->glfw
  [button]
  (or (mouse-button->gl button)
      (throw (ex-info "Unknown mouse button" {:button button}))))

(defn gamepad-button->glfw
  "Gamepad button keyword → GLFW index for [[quest.cyberdungeon.glfw.gamepad-state/button]]."
  [button]
  (or (gamepad-button->gl button)
      (throw (ex-info "Unknown gamepad button" {:button button}))))

(defn gamepad-axis->glfw
  "Gamepad axis keyword → GLFW index for [[quest.cyberdungeon.glfw.gamepad-state/axis]]."
  [axis]
  (or (gamepad-axis->gl axis)
      (throw (ex-info "Unknown gamepad axis" {:axis axis}))))

(defn action-keyword
  "GLFW callback action → `:press`, `:release`, or nil (e.g. key repeat)."
  [action]
  (gl->action (int action)))

(defn action->keyword
  "GLFW callback or poll action → `:press` or `:release`; throws if unknown."
  [action]
  (or (action-keyword action)
      (throw (ex-info "Unknown input action" {:action action}))))

(defn joystick-count
  "Joystick ids to probe: `(range (joystick-count))`."
  []
  (inc GLFW/GLFW_JOYSTICK_LAST))

;; --- library lifecycle ---

(defn- init!
  []
  (GLFW/glfwInit))

(defn- terminate!
  []
  (GLFW/glfwTerminate))

(defn with-state*
  "Internal helper for [[with-state]]; callable from expanded macro code in other namespaces."
  [f]
  (when-not (init!)
    (throw (ex-info "Unable to initialize GLFW" {})))
  (try
    (f)
    (finally
      (terminate!))))

(defmacro with-state
  "Run `body` forms with GLFW initialized; terminates in `finally`."
  [& body]
  `(with-state* (fn [] ~@body)))

(defn poll-events!
  "Process pending window and input events. Call each frame before drawing; may invoke callbacks
  registered via `set-*-callback!`."
  []
  (GLFW/glfwPollEvents))

(defn swap-interval!
  "Set swap interval for the **current** context's window: `0` = vsync off, `1` = vsync on.
  Call after [[make-context-current!]]."
  [value]
  (GLFW/glfwSwapInterval value))

(defn get-time
  "Monotonic time in seconds since GLFW was initialized (e.g. inside [[with-state]]). Useful for frame timing."
  []
  (GLFW/glfwGetTime))

;; --- window ---

(def ^:private true* GLFW/GLFW_TRUE)
(def ^:private false* GLFW/GLFW_FALSE)
(def ^:private opengl-core-profile GLFW/GLFW_OPENGL_CORE_PROFILE)

(def ^:private window-hint-name->gl
  {:context-version-major GLFW/GLFW_CONTEXT_VERSION_MAJOR
   :context-version-minor GLFW/GLFW_CONTEXT_VERSION_MINOR
   :opengl-profile GLFW/GLFW_OPENGL_PROFILE
   :opengl-forward-compat GLFW/GLFW_OPENGL_FORWARD_COMPAT})

(def ^:private window-hint-value->gl
  {true true*
   false false*
   :opengl-core-profile opengl-core-profile})

(defn- window-hint!
  [hint value]
  (GLFW/glfwWindowHint hint value))

(defn- resolve-window-hint-name
  [hint]
  (or (window-hint-name->gl hint)
      (throw (ex-info "Unknown window hint" {:hint hint}))))

(defn- resolve-window-hint-value
  [value]
  (if (contains? window-hint-value->gl value)
    (window-hint-value->gl value)
    value))

(defn window-hints!
  "Set window/context hints before [[with-window]]. `hints` uses **keywords** for hint names
  and Clojure values where possible; not persisted across windows.

  Example (OpenGL 3.2 core, forward-compatible on macOS):

      {:context-version-major 3
       :context-version-minor 2
       :opengl-profile :opengl-core-profile
       :opengl-forward-compat true}"
  [hints]
  (doseq [[hint value] hints]
    (window-hint! (resolve-window-hint-name hint)
                  (resolve-window-hint-value value))))

(defn- create-window!
  [width height title monitor share]
  (GLFW/glfwCreateWindow (int width)
                         (int height)
                         (str title)
                         (long monitor)
                         (long share)))

(defn- destroy-window!
  [window]
  (GLFW/glfwDestroyWindow window))

(defn with-window*
  "Internal helper for [[with-window]]; callable from expanded macro code in other namespaces."
  [{:keys [width height title monitor share]} f]
  (let [window (create-window! width height title monitor share)]
    (when (zero? window)
      (throw (ex-info "Couldn't create window" {:width width :height height :title title})))
    (try
      (f window)
      (finally
        (destroy-window! window)))))

(defmacro with-window
  "Create a window and bind it for `body`, like `clojure.core/with-open` / `clojure.java.jdbc/with-db-connection`.

  `binding` is `[sym cfg]`: `sym` is the handle in `body`; `cfg` requires `:width`, `:height`,
  `:title`, `:monitor`, and `:share` (`0` = windowed / no shared context).

      (with-window [w {:width 640 :height 480 :title \"App\" :monitor 0 :share 0}]
        (make-context-current! w)
        ...)

  Call [[window-hints!]] first. Free callbacks with [[quest.cyberdungeon.glfw.callbacks/free!]]
  before the window is destroyed if you registered any."
  [[window cfg] & body]
  `(with-window* ~cfg (fn [~window] ~@body)))

(defn window-should-close?
  "Whether the user requested close (e.g. close box). Poll in your main loop."
  [window]
  (GLFW/glfwWindowShouldClose window))

(defn set-window-should-close!
  "Set the close flag (e.g. from `:escape` in [[set-key-callback!]])."
  [window value]
  (GLFW/glfwSetWindowShouldClose window value))

(defn make-context-current!
  "Make `window`'s OpenGL context current on this thread. Required before [[swap-buffers!]]
  and [[swap-interval!]]."
  [window]
  (GLFW/glfwMakeContextCurrent window))

(defn swap-buffers!
  "Swap front and back buffers for `window`. Call after rendering each frame."
  [window]
  (GLFW/glfwSwapBuffers window))

(defn- get-framebuffer-size*
  [window w h]
  (GLFW/glfwGetFramebufferSize ^long window w h))

(defn get-framebuffer-size
  "Framebuffer size in **pixels** as `[width height]`. Use for Retina scaling vs [[get-window-size]]."
  [window]
  (let [w (int-array 1)
        h (int-array 1)]
    (get-framebuffer-size* window w h)
    [(aget w 0) (aget h 0)]))

(defn- get-window-size*
  [window w h]
  (GLFW/glfwGetWindowSize ^long window w h))

(defn get-window-size
  "Window size in **screen coordinates** as `[width height]`. Cursor from [[get-cursor-pos]] uses
  this space; compare to [[get-framebuffer-size]] on Retina."
  [window]
  (let [w (int-array 1)
        h (int-array 1)]
    (get-window-size* window w h)
    [(aget w 0) (aget h 0)]))

;; --- callbacks ---

(defn set-framebuffer-size-callback!
  "Register resize callback `(fn [window width height] ...)`. Only one callback per type per
  window — pair teardown with [[quest.cyberdungeon.glfw.callbacks/free!]]."
  [window f]
  (GLFW/glfwSetFramebufferSizeCallback window f))

(defn set-key-callback!
  "Register key callback `(fn [window key scancode action mods] ...)`. `key` is a keyword
  (e.g. `:escape`, `:a`). Use [[action->keyword]] on `action`. See also [[get-key]]."
  [window f]
  (GLFW/glfwSetKeyCallback
   window
   (fn [window key scancode action mods]
     (f window (key-code->keyword key) scancode action mods))))

(defn set-cursor-pos-callback!
  "Register cursor move callback `(fn [window x y] ...)` in window coordinates."
  [window f]
  (GLFW/glfwSetCursorPosCallback window f))

(defn set-mouse-button-callback!
  "Register mouse button callback `(fn [window button action mods] ...)`. `button` is a keyword
  (`:mouse-left`, etc.). Use [[action->keyword]] on `action`."
  [window f]
  (GLFW/glfwSetMouseButtonCallback
   window
   (fn [window button action mods]
     (f window (mouse-button-code->keyword button) action mods))))

;; --- input ---

(defn get-key
  "Poll keyboard: returns `:press` or `:release` for key keyword (e.g. `:escape`). Alternative
  to [[set-key-callback!]] for held-key checks."
  [window key]
  (action->keyword
   (GLFW/glfwGetKey window (int (key->glfw key)))))

(defn- get-cursor-pos*
  [window x y]
  (GLFW/glfwGetCursorPos window x y))

(defn get-cursor-pos
  "Cursor position in window coordinates as `[x y]` (doubles)."
  [window]
  (let [x (double-array 1)
        y (double-array 1)]
    (get-cursor-pos* window x y)
    [(aget x 0) (aget y 0)]))

(defn get-mouse-button
  "Poll mouse button: returns `:press` or `:release` for `:mouse-left`, `:mouse-right`, or
  `:mouse-middle`."
  [window button]
  (action->keyword
   (GLFW/glfwGetMouseButton window (int (mouse-button->glfw button)))))

;; --- cursor ---

(defn set-cursor!
  "Set the cursor image for `window`. `cursor` from [[create-cursor!]] or `nil` for default."
  [window cursor]
  (GLFW/glfwSetCursor window cursor))

(defn destroy-cursor!
  "Free a cursor from [[create-cursor!]]."
  [cursor]
  (GLFW/glfwDestroyCursor cursor))

(defn create-cursor!
  "Create a custom cursor from a [[quest.cyberdungeon.glfw.image/malloc]] image after
  [[quest.cyberdungeon.glfw.image/width!]], [[quest.cyberdungeon.glfw.image/height!]], and
  [[quest.cyberdungeon.glfw.image/pixels!]]. Hotspot is the click point in pixels.
  Install with [[set-cursor!]]; free cursor with [[destroy-cursor!]] and image with
  [[quest.cyberdungeon.glfw.image/free!]]."
  [image hotspot-x hotspot-y]
  (GLFW/glfwCreateCursor image hotspot-x hotspot-y))

;; --- joystick / gamepad ---

(defn joystick-present?
  "Whether joystick id `jid` is connected."
  [jid]
  (GLFW/glfwJoystickPresent jid))

(defn joystick-is-gamepad?
  "Whether joystick `jid` has a mapped gamepad layout (see [[get-gamepad-state]])."
  [jid]
  (GLFW/glfwJoystickIsGamepad jid))

(defn get-gamepad-state
  "Fill `state` (from [[quest.cyberdungeon.glfw.gamepad-state/calloc]]) for gamepad `jid`.
  Read buttons/axes with keywords (e.g. `:a`, `:left-x`) via [[quest.cyberdungeon.glfw.gamepad-state/button]]
  and [[quest.cyberdungeon.glfw.gamepad-state/axis]]."
  [jid state]
  (GLFW/glfwGetGamepadState jid state))

(defn get-joystick-buttons
  "Raw button buffer for joystick `jid` when not using [[get-gamepad-state]]."
  [jid]
  (GLFW/glfwGetJoystickButtons jid))

(defn get-joystick-axes
  "Raw axis buffer for joystick `jid` when not using [[get-gamepad-state]]."
  [jid]
  (GLFW/glfwGetJoystickAxes jid))
