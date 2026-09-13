(ns quest.cyberdungeon.glfw.glfw
  "Clojure facade for GLFW 3.3 windowing and input.

  Typical lifecycle: [[with-state]] (init + terminate) → [[window-hint!]] (optional, repeatable) →
  [[create-window!]] → [[make-context-current!]] → loop of [[poll-events!]] and [[swap-buffers!]] →
  [[destroy-window!]].

  Register input with the `set-*-callback!` functions; release native callback slots with
  [[quest.cyberdungeon.glfw.callbacks/free!]] when done.

  Portable JAR only — applications must add `lwjgl-glfw` (and usually `lwjgl`) **native**
  classifiers for their OS.

  **Load-time natives:** top-level `def`s read `GLFW/…` static fields when this namespace
  is loaded (`require`, REPL, Codox, etc.). That initializes LWJGL and loads JNI for the
  current platform. Tools that only need docstrings (e.g. Codox on CI) must still put the
  matching native JARs on the classpath."
  (:import (org.lwjgl.glfw GLFW)))

;; --- constants ---

(def true*
  "Boolean hint value (`1`). Use with [[window-hint!]], e.g. [[opengl-forward-compat]]."
  GLFW/GLFW_TRUE)

(def context-version-major
  "Window hint name: OpenGL major version. Set via [[window-hint!]] before [[create-window!]]."
  GLFW/GLFW_CONTEXT_VERSION_MAJOR)

(def context-version-minor
  "Window hint name: OpenGL minor version. Pair with [[context-version-major]] via [[window-hint!]]."
  GLFW/GLFW_CONTEXT_VERSION_MINOR)

(def opengl-profile
  "Window hint name: OpenGL profile. Value is often [[opengl-core-profile]] via [[window-hint!]]."
  GLFW/GLFW_OPENGL_PROFILE)

(def opengl-core-profile
  "Window hint value: OpenGL core profile. Use with [[opengl-profile]] and [[window-hint!]]."
  GLFW/GLFW_OPENGL_CORE_PROFILE)

(def opengl-forward-compat
  "Window hint name: forward-compatible context. Pass [[true*]] to [[window-hint!]] (common on macOS)."
  GLFW/GLFW_OPENGL_FORWARD_COMPAT)

(def key-escape
  "Keyboard key token for [[get-key]] / [[set-key-callback!]]. Action: [[press]] or [[release]]."
  GLFW/GLFW_KEY_ESCAPE)

(def key-space
  "Keyboard key token for [[get-key]] / [[set-key-callback!]]. Action: [[press]] or [[release]]."
  GLFW/GLFW_KEY_SPACE)

(def key-enter
  "Keyboard key token for [[get-key]] / [[set-key-callback!]]. Action: [[press]] or [[release]]."
  GLFW/GLFW_KEY_ENTER)

(def key-tab
  "Keyboard key token for [[get-key]] / [[set-key-callback!]]. Action: [[press]] or [[release]]."
  GLFW/GLFW_KEY_TAB)

(def key-backspace
  "Keyboard key token for [[get-key]] / [[set-key-callback!]]. Action: [[press]] or [[release]]."
  GLFW/GLFW_KEY_BACKSPACE)

(def key-delete
  "Keyboard key token for [[get-key]] / [[set-key-callback!]]. Action: [[press]] or [[release]]."
  GLFW/GLFW_KEY_DELETE)

(def key-minus
  "Keyboard key token for [[get-key]] / [[set-key-callback!]]. Action: [[press]] or [[release]]."
  GLFW/GLFW_KEY_MINUS)

(def key-equal
  "Keyboard key token for [[get-key]] / [[set-key-callback!]]. Action: [[press]] or [[release]]."
  GLFW/GLFW_KEY_EQUAL)

(def key-left
  "Keyboard key token for [[get-key]] / [[set-key-callback!]]. Action: [[press]] or [[release]]."
  GLFW/GLFW_KEY_LEFT)

(def key-right
  "Keyboard key token for [[get-key]] / [[set-key-callback!]]. Action: [[press]] or [[release]]."
  GLFW/GLFW_KEY_RIGHT)

(def key-up
  "Keyboard key token for [[get-key]] / [[set-key-callback!]]. Action: [[press]] or [[release]]."
  GLFW/GLFW_KEY_UP)

(def key-down
  "Keyboard key token for [[get-key]] / [[set-key-callback!]]. Action: [[press]] or [[release]]."
  GLFW/GLFW_KEY_DOWN)

(def press
  "Key or mouse button is down. Return value of [[get-key]], [[get-mouse-button]], and callback `action`."
  GLFW/GLFW_PRESS)

(def release
  "Key or mouse button was released. Callback `action` with [[set-key-callback!]] / [[set-mouse-button-callback!]]."
  GLFW/GLFW_RELEASE)

(def mouse-button-left
  "Mouse button token for [[get-mouse-button]] / [[set-mouse-button-callback!]]. With [[press]] / [[release]]."
  GLFW/GLFW_MOUSE_BUTTON_LEFT)

(def mouse-button-right
  "Mouse button token for [[get-mouse-button]] / [[set-mouse-button-callback!]]. With [[press]] / [[release]]."
  GLFW/GLFW_MOUSE_BUTTON_RIGHT)

(def mouse-button-middle
  "Mouse button token for [[get-mouse-button]] / [[set-mouse-button-callback!]]. With [[press]] / [[release]]."
  GLFW/GLFW_MOUSE_BUTTON_MIDDLE)

(def joystick-last
  "Highest GLFW joystick id (inclusive). Loop `0`..`joystick-last` with [[joystick-present?]]."
  GLFW/GLFW_JOYSTICK_LAST)

(def gamepad-button-left-bumper
  "Gamepad button index for [[quest.cyberdungeon.glfw.gamepad-state/button]] after [[get-gamepad-state]]."
  GLFW/GLFW_GAMEPAD_BUTTON_LEFT_BUMPER)

(def gamepad-button-right-bumper
  "Gamepad button index for [[quest.cyberdungeon.glfw.gamepad-state/button]] after [[get-gamepad-state]]."
  GLFW/GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER)

(def gamepad-button-a
  "Gamepad button index for [[quest.cyberdungeon.glfw.gamepad-state/button]] after [[get-gamepad-state]]."
  GLFW/GLFW_GAMEPAD_BUTTON_A)

(def gamepad-button-x
  "Gamepad button index for [[quest.cyberdungeon.glfw.gamepad-state/button]] after [[get-gamepad-state]]."
  GLFW/GLFW_GAMEPAD_BUTTON_X)

(def gamepad-axis-left-x
  "Gamepad axis index for [[quest.cyberdungeon.glfw.gamepad-state/axis]] after [[get-gamepad-state]]."
  GLFW/GLFW_GAMEPAD_AXIS_LEFT_X)

(def gamepad-axis-left-y
  "Gamepad axis index for [[quest.cyberdungeon.glfw.gamepad-state/axis]] after [[get-gamepad-state]]."
  GLFW/GLFW_GAMEPAD_AXIS_LEFT_Y)

;; --- library lifecycle ---

(defn- init!
  []
  (GLFW/glfwInit))

(defn- terminate!
  []
  (GLFW/glfwTerminate))

(defn- with-state*
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

(defn window-hint!
  "Set one window/context hint before [[create-window!]]. Hints are not persisted across
  windows — set again for each creation.

  Hint **names** include [[context-version-major]], [[context-version-minor]],
  [[opengl-profile]], [[opengl-forward-compat]]. Hint **values** include [[true*]],
  [[opengl-core-profile]], and numeric versions."
  [hint value]
  (GLFW/glfwWindowHint hint value))

(defn create-window!
  "Create a window and OpenGL context. Returns window handle, or `0` on failure.

  Set hints with [[window-hint!]] first. `monitor` `0` = windowed; `share` `0` = no shared
  context. Then [[make-context-current!]], [[swap-interval!]], and register callbacks."
  [width height title monitor share]
  (GLFW/glfwCreateWindow (int width)
                         (int height)
                         (str title)
                         (long monitor)
                         (long share)))

(defn destroy-window!
  "Destroy `window` and its context. Call [[quest.cyberdungeon.glfw.callbacks/free!]] first if
  callbacks were registered."
  [window]
  (GLFW/glfwDestroyWindow window))

(defn window-should-close?
  "Whether the user requested close (e.g. close box). Poll in your main loop."
  [window]
  (GLFW/glfwWindowShouldClose window))

(defn set-window-should-close!
  "Set the close flag (e.g. from [[key-escape]] in [[set-key-callback!]])."
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

(defn get-framebuffer-size
  "Write framebuffer size in **pixels** into `w` and `h` (each a length-1 int array).
  Use for Retina scaling vs [[get-window-size]]."
  [window w h]
  (GLFW/glfwGetFramebufferSize ^long window w h))

(defn get-window-size
  "Write window size in **screen coordinates** into `w` and `h` (each a length-1 int array).
  Cursor from [[get-cursor-pos]] uses this space; compare to [[get-framebuffer-size]] on Retina."
  [window w h]
  (GLFW/glfwGetWindowSize ^long window w h))

;; --- callbacks ---

(defn set-framebuffer-size-callback!
  "Register resize callback `(fn [window width height] ...)`. Only one callback per type per
  window — pair teardown with [[quest.cyberdungeon.glfw.callbacks/free!]]."
  [window f]
  (GLFW/glfwSetFramebufferSizeCallback window f))

(defn set-key-callback!
  "Register key callback `(fn [window key scancode action mods] ...)`. `action` is [[press]]
  or [[release]]. See also polling with [[get-key]]."
  [window f]
  (GLFW/glfwSetKeyCallback window f))

(defn set-cursor-pos-callback!
  "Register cursor move callback `(fn [window x y] ...)` in window coordinates."
  [window f]
  (GLFW/glfwSetCursorPosCallback window f))

(defn set-mouse-button-callback!
  "Register mouse button callback `(fn [window button action mods] ...)`. `button` uses
  [[mouse-button-left]] etc.; `action` is [[press]] or [[release]]."
  [window f]
  (GLFW/glfwSetMouseButtonCallback window f))

;; --- input ---

(defn get-key
  "Poll keyboard: returns [[press]] or [[release]] for `key` (e.g. [[key-escape]]). Alternative
  to [[set-key-callback!]] for held-key checks."
  [window key]
  (GLFW/glfwGetKey window (int key)))

(defn get-cursor-pos
  "Write cursor position into `x` and `y` (double arrays length 1), in window coordinates."
  [window x y]
  (GLFW/glfwGetCursorPos window x y))

(defn get-mouse-button
  "Poll mouse button: returns [[press]] or [[release]] for `button` (e.g. [[mouse-button-left]])."
  [window button]
  (GLFW/glfwGetMouseButton window button))

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
  Read buttons with [[quest.cyberdungeon.glfw.gamepad-state/button]] and axes with
  [[quest.cyberdungeon.glfw.gamepad-state/axis]] using [[gamepad-button-a]] etc."
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
