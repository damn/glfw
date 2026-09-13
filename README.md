# damn/glfw

Clojure namespaces over [LWJGL](https://www.lwjgl.org/) `org.lwjgl.glfw` (GLFW 3.3).

## Dependencies

This library depends only on the portable `lwjgl-glfw` JAR. **Applications** must add native classifiers for their OS, for example:

```clojure
[org.lwjgl/lwjgl-glfw "3.3.3" :classifier "natives-macos"]
;; natives-linux | natives-linux-arm64 | natives-macos-arm64 | natives-windows | …
```

## Usage

```clojure
(require '[damn.glfw.glfw :as glfw])

(when (glfw/init!)
  (try
    ;; window hints, create-window!, poll-events!, …
    (finally
      (glfw/terminate!))))
```

## Window smoke test

```bash
lein window-test
```

Requires `:dev` profile natives (`lwjgl` + `lwjgl-glfw` classifiers; macOS in `project.clj` — adjust for your platform).

## Docs

```bash
lein codox
```

Output: `target/doc/index.html`
