(ns tray-racer.test.math
  (:use [tray-racer.math] :reload)
  (:use [clojure.test]))

;XXX fix these test with a regex

(deftest stupid-test 
         (testing "some stupid bullshit")
         (let [[2 3 5] x]
           (println x)
           (println (normalize x))
           (println (length x))
           (println (dot x (vec3. 1 2 3)))
           (println (cross x (vec3. 1 1 1)))
           (println (add x (vec3. 2 2 2)))
           (println (minus x (vec3. 1 1 1)))
           (println (mul x (vec3. 3 3 3 )))
           (println (mul x 7))
           (println (mul x 3.5)))


           ;dummy test
           (is (= 3 (+ 2 1))))

