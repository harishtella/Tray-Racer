(ns tray-racer.core
  (:require [tray-racer.ray-tracer :as rt])
  (:use alex-and-georges.debug-repl)
  (:use [rosado.processing] [rosado.processing.applet]))

(defn draw []
  (rt/fire))

(defn setup []
  "Runs once."
  (smooth)
  (background-float 225)
  (stroke-float 10)
  ;; call the draw function as often as possible 
  (framerate 9999)) 

(defapplet tr :title "tray-racer: best graphics"
  :setup setup :draw draw :size rt/real-d)
  
(defn -main [& args] 
  (run tr))

