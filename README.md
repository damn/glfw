# glfw

Clojure namespaces `quest.cyberdungeon.glfw.*` over [LWJGL](https://www.lwjgl.org/) `org.lwjgl.glfw` (GLFW 3.3).

## Dependency (JitPack)

```clojure
:repositories [["jitpack" "https://jitpack.io"]]

[com.github.damn/glfw "v0.1.1"]
```

Replace the version with a [release tag](https://github.com/damn/glfw/tags) or commit hash JitPack has built (see [jitpack.io/#damn/glfw](https://jitpack.io/#damn/glfw)).

**Natives** are not bundled — add platform classifiers in your application:

```clojure
[org.lwjgl/lwjgl-glfw "3.3.3" :classifier "natives-macos"]
;; natives-linux | natives-linux-arm64 | natives-macos-arm64 | natives-windows | …
```

## Usage

```clojure
(require '[quest.cyberdungeon.glfw.glfw :as glfw])

(when (glfw/init!)
  (try
    ;; …
    (finally
      (glfw/terminate!))))
```

## Window smoke test

```bash
lein window-test
```

Uses `:dev` profile natives (`project.clj` defaults to macOS — change for your OS).

## API documentation

**Docstrings live in the Clojure sources** (`src/quest/cyberdungeon/glfw/`). Do not duplicate them in this README.

| Approach | Role |
|----------|------|
| **README** (this file) | Install, JitPack coordinates, links — not a full API reference |
| **Codox → GitHub Pages** | Generated HTML from docstrings; linked below |
| **GitHub source browser** | Shows docstrings on each `.clj` file when browsing the repo |

**Published docs:** [https://damn.github.io/glfw/](https://damn.github.io/glfw/) (Codox output from CI on `main` → `gh-pages` branch).

If the site 404s after the first workflow run, enable Pages once: **Settings → Pages → Build and deployment → Deploy from branch → `gh-pages` / `/`**, or use the GitHub API to create the Pages site.

Local: `lein codox` → open `target/doc/index.html`.
