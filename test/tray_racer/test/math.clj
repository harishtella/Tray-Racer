(ns tray-racer.test.math
  (:use [tray-racer.math] :reload)
  (:use [clojure.test])
  (:import [tray-racer.math vec3]))

(deftest stupid-test 
         (testing "some stupid bullshit")
         (let [x (vec3. 2 3 5)]
           (println x)
           (println (normalize x))
           (println (length x))
           (println (dot x (vec3. 1 2 3)))
           (println (cross x (vec3. 1 1 1)))
           (println (add x (vec3. 2 2 2)))
           (println (minus x (vec3. 1 1 1)))
           (println (mul x (vec3. 3 3 3 )))
           (println (mul x 7))
           (println (mul x 3.5))

           ; should cause exception
           ;(println (mul x "hi"))
           )

         (is (= 3 (+ 2 1))))

