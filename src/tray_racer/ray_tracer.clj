(ns tray-racer.ray-tracer
  (:require [clojure.contrib.math :as m])
  (:require [clojure.pprint :as p])
  (:require [tray-racer.vec3 :as v])
  (:require [tray-racer.scene :as s])
  (:use tray-racer.utils)
  (:use clojure.stacktrace)
  (:use alex-and-georges.debug-repl)
  (:use [rosado.processing]))


;; COORDINATES
;; needed for ray-tracing 
;; ---------------------------------------------
;;
;;
;;

; origin of rays to be traced in world coordinates
(def o [0 0 -5])

; projection plane location world coordinates 
(def proj-plane-location [[-4 4] [3 -3] 3])

; dimensions of program window in screen pixels
(def window-dim [200 150])

; A list of all the screen window coordinates. 
; It is used for calculating color values. 
;
(def window-coords
  (let [[rx ry] (map dec window-dim)]
    (letfn [(gen-next-coord [[x y]]
                           (cond
                             (and (>= x rx) (>= y ry)) nil
                             (>= y ry)  [(inc x) 0]
                             :else [x (inc y)]) )]
      (iterate-until-end gen-next-coord [0 0]))))


;; RAYS
;; ---------------------------------------------
;;
;;
;;

(defrecord Ray [orig dir])

;; returns a point along the ray
(defn ray-point [ray t]
  (v/+ (v/* (:dir ray) t)
       (:orig ray)))


;; RAY-TRACING
;; ---------------------------------------------
;;
;;
;;


;; Given coord, a point in screen pixels on program window,
;; returns a point in world coordinates on the projection
;; plane.
(defn real-coord->proj-coord [coord]
  (let [ratios (map / coord window-dim)
        px (map-to-range (nth ratios 0) (nth proj-plane-location 0))
        py (map-to-range (nth ratios 1) (nth proj-plane-location 1))
        pz (nth proj-plane-location 2)]
    (vector px py pz)))
      
; Returns a vector with:
; 1. the first primitive hit by ray
; 2. the point at which it is hit 
; If the ray doesn't not hit any primitives nil is returned 
(defn first-prim-hit-by [ray] 
  (if-let [first-prim-hit (->>
                   @s/Scene
                   (map #(s/intersect % ray))
                   (filter #(number? (second %)))
                   (sort-by second <)
                   (first))]
    [(first first-prim-hit) (ray-point ray (second first-prim-hit))]
    ; XXX make intersect return something less messy
    nil))

(defn c1->c255 [x]
  (map-to-range x [0 255]))

(def full-bright-color [1 1 1])
(def no-color [0 0 0])

; Returns the color contributed by light at hit-point. 
(defn color-from-light [prim hit-point n light]
  (let [l (v/norm (v/- (:center light) hit-point))
        light-incidence (v/dot n l)
        diffuse-component (* light-incidence (s/mat-prop :diffuse prim))]
    (if (> diffuse-component 0) 
      (reduce v/* [diffuse-component 
                   (s/mat-prop :color light) 
                   (s/mat-prop :color prim)])
      no-color)))

; Return the color at hit-point.
(defn calc-color [[prim hit-point]]
  (if (:is-light prim) 
    (map c1->c255 full-bright-color)
    (let [n (s/get-normal prim hit-point)
          lights (s/get-lights)
          color (reduce v/+ 
                        (map (partial color-from-light prim hit-point n)
                             lights))]
      (map c1->c255 color))))


; Takes in a window coordinate, translates it to a point on
; the projection plane, traces a ray that goes through that
; point computing the resulting color, then finally it sets
; the window coordinate to this color. 
;
(defn fire-ray-from [[x y]]
    (let [proj-coord (real-coord->proj-coord [x y])
          ray (Ray. o proj-coord)]
      (if-let [hit (first-prim-hit-by ray)]
        (let [color-val (calc-color hit)]
          (set-pixel x y (apply color color-val)))
        (set-pixel x y (apply color no-color)))))

; Fires the next ray to be traced.
;
(def fire 
  (let [delayed-firings (atom (map fire-ray-from window-coords))]
    (fn [] (do-one delayed-firings))))

