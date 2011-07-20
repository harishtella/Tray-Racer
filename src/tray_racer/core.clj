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
  (:import (processing.core PApplet)))

(defn setup []
  "Runs once."
  (apply size rt/window-dim)
  (smooth)
  (stroke-float 10)

  ;; Setting framerate to a really high value gets the draw
  ;; function to be called as often as possible 
  ;;
  (framerate 9999999) 

  ;; Initialize to the scene to be ray-traced
  ;;
  (s/init-scene))

(defn draw []
  "Gets called every time a frame is to be drawn, Draw in turn
  calls the fire function in ray-tracer.clj "
  (rt/fire))

(def swing-frame (JFrame. "Ray tracing action"))
(def p-app
     (proxy [PApplet] []
       (setup []
              (binding [*applet* this]
                (setup)))
       (draw []
             (binding [*applet* this]
               (draw)))))

(defn -main [& args] 
  (.init p-app)
  (doto swing-frame
    (.setResizable false)
    (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
    (.setSize (rt/window-dim 0) (rt/window-dim 1))
    (.add p-app)
    (.pack)
    (.show)))

