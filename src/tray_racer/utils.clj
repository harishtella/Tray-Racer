;; utils.clj 
;; ----------------------------------
;; some misc utils functions

(ns tray-racer.utils
 (:import (processing.core PApplet))
 (:use [rosado.processing]))

;; converts a list of RGB values 
;; to a processing.org color value 
(def color-helper 
  (let [temp-app (PApplet.)]
    (fn [rgb-list]
      (binding [*applet* temp-app]
        (apply color rgb-list)))))

(defn partition-with-leftovers [chunk-size a-seq]
  "Splits a-seq into a sequence of lists, where each list
  is chunk-size long. In case there are not enough
  elements, returns a partition with less than n items."
  (partition chunk-size chunk-size () a-seq))

(defn map-to-range [t [r1 r2]]
  {:pre [(and (>= t 0) (<= t 1))]
   :post [(number? %)]}
  "Maps t, a value between 0 and 1, 
  to a value between r1 and r2."
  (+ r1 (* t (- r2 r1))))


(defn iterate-until-end [f x] 
  "Generates a lazy sequence that starts with x and with
  subsequent terms generated by applying f to the previous
  term. If f applied to the previous term returns nil then
  the sequence ends with the previous term."
  (let [next-x (f x)]
    (if (= next-x nil)
      (list x)
      (cons x (lazy-seq (iterate-until-end f next-x))))))

 
(defn do-one [todos]
  "Takes in an atom containing a list to be evaluated for its
  side-effects.  do-one evaluates the first item of this
  list and then sets the atom to contain the rest of the
  unevaluated list."
  (let [t (first @todos)]
    (swap! todos rest)))

