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

;; converts a list of RGB values to processing.org 
;; color value 
(def color-helper 
  (let [temp-app (PApplet.)]
    (fn [rgb-list]
      (binding [*applet* temp-app]
        (apply color rgb-list)))))

;; initial state of the screen pixels: all black
(def agent-init-state (vec 
                        (repeat (reduce * rt/window-dim) 
                                (color-helper [0 0 0]))))

(def ray-tracing-agent (agent agent-init-state))

;; the object that actually gets drawn to the screen
(def canvas (PImage. (rt/window-dim 0) (rt/window-dim 1) RGB))
 
(defn my-setup []
  "Runs once."
  (apply size rt/window-dim)
  (smooth)
  (stroke-float 10)
  (framerate 15) 

  (s/init-scene)
  (rt/start-up ray-tracing-agent color-helper))

(defn my-draw []
  "sets the canvas contents to be the current state of the agent 
  and then draws the canvas to the screen"
  (let [calculated-pixels @ray-tracing-agent
        calculated-pixels (int-array calculated-pixels)]
    (.loadPixels canvas)
    (set! (.pixels canvas) calculated-pixels)
    (.updatePixels canvas)
    (image canvas 0 0)))

(def swing-frame (JFrame. "Tray-Racer"))
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

