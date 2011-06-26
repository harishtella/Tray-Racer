;; core.clj 
;; ----------------------------------
;; the main application file

(ns tray-racer.core
  (:gen-class)
  (:require [tray-racer.ray-tracer :as rt])
  (:require [tray-racer.scene :as s])
  (:use alex-and-georges.debug-repl)
  (:use [rosado.processing] [rosado.processing.applet]))


(defn setup []
  "Runs once."
  (smooth)
  (background-float 225)
  (stroke-float 10)

  ;; Setting framerate to a really high value gets the draw
  ;; function to be called as often as possible 
  ;;
  (framerate 9999999) 

  ;; Initialize to the scene to be ray-traced
  ;;
  (s/init-scene))



(defn draw []
  "Gets called everytime a frame is to be drawn, Draw in turn
  calls the fire function in ray-tracer.clj "
  (rt/fire))

(defapplet tr :title "tray-racer: number one super graphics"
  :setup setup :draw draw :size rt/window-dim)
 
(defn -main [& args] 
  (run tr))

