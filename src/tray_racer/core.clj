;; core.clj 
;; ----------------------------------
;; the main application file

(ns tray-racer.core
  (:gen-class)
  (:require [tray-racer.ray-tracer :as rt])
  (:require [tray-racer.scene :as s])
  (:use alex-and-georges.debug-repl)
  (:use [rosado.processing])
  (:import (javax.swing JFrame))
  (:import (processing.core PApplet))
  (:import (processing.core PImage)))

(def ray-tracing-agent (agent {}))
(def canvas (PImage. 100 75 RGB))

(defn my-setup []
  "Runs once."
  (apply size rt/window-dim)
  (smooth)
  (stroke-float 10)
  (framerate 5) 

  (def canvas (load-image "f.jpg"))

  ;; Initialize to the scene to be ray-traced
  ;;
  (s/init-scene)
  (rt/start-up ray-tracing-agent))

(defn my-draw []
  "Gets called every time a frame is to be drawn, Draw in turn
  calls the fire function in ray-tracer.clj "
  (let [calculated-pixels @ray-tracing-agent]
    (.loadPixels canvas)
    (dorun 
      (map (fn [[[x y] color-val]] 
             (let [index (+ (* y (rt/window-dim 0)) x)
                   color-val (apply color color-val)]
               (aset (.pixels canvas) index color-val)))
           calculated-pixels))
    (.updatePixels canvas)
    (image canvas 0 0)))

(def swing-frame (JFrame. "Ray tracing action"))
(def p-app
     (proxy [PApplet] []
       (setup []
              (binding [*applet* this]
                (my-setup)))
       (draw []
             (binding [*applet* this]
               (my-draw)))))

(defn -main [& args] 
  (.init p-app)
  (doto swing-frame
    (.setResizable false)
    (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
    (.setSize (rt/window-dim 0) (rt/window-dim 1))
    (.add p-app)
    (.pack)
    (.show)))

