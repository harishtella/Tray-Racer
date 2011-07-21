(ns tray-racer.test.utils
  (:use [tray-racer.utils] :reload)
  (:use [clojure.test]))
  
(defn isnt [x] (is (not x)))

(deftest mapping-to-range 
         (is (= (map-to-range 0.3 [-1 1]) -0.4))
         (is (= (map-to-range 0.4 [4 9]) 6.0))
         (is (thrown? java.lang.AssertionError 
                      (map-to-range 5 [2 3]))))

(deftest test-iterate-until-end
         (let [test-seq (iterate-until-end 
                          (fn [x] (if (< x 2) (+ x 1) nil)) 
                          1)]
           (is (= (first test-seq) 1))
           (is (= (second test-seq) 2))
           (is (= (nnext test-seq) nil))))



           


