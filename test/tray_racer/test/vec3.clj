(ns tray-racer.test.vec3
  (:use [tray-racer.vec3] :reload)
  (:refer-clojure :rename {+ cc+ - cc- * cc*})
  (:use [clojure.test]))
  
(defn isnt [x] (is (not x)))

(deftest verifying-vectors
         (is (vec3? [1 2 3]))
         (is (vec3? [1.2 1.3 5.3]))
         (is (vec3? [1 3 2] [3.5 2.3 5.3]))
         (isnt (vec3? [1 2 3 4]))
         (isnt (vec3? [1]))
         (isnt (vec3? (list 1 2 3)))
         (isnt (vec3? [1 2 'z]))
         (isnt (vec3? [1 2 3] [1 'z 2])))

(deftest length
         (is (= (len [1 2 3]) 
                3.7416573867739413)))

(deftest normal
         (is (= (norm [3 5 9]) [0.27975144247209416 
                                0.4662524041201569 
                                0.8392543274162825])))

(deftest dot-product
         (is (= (dot [1 2 3] [4 5 6]) 32)))

(deftest cross-product
         (is (= (cross [1 2 3] [4 5 6]) [30 54 12])))

(deftest vector-addition
         (is (= (+ [1 2 3] [4 5 6]) [5 7 9])))

(deftest vector-subtraction
         (is (= (- [1 2 3] [4 5 6]) [-3 -3 -3])))

(deftest vector-multiplication
         (is (= (* [1 2 3] [4 5 6]) [4 10 18]))
         (is (= (* 2 [3 4 5]) [6 8 10]))
         (is (= (* [3 4 5] 2) [6 8 10])))



