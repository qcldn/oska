(ns oska.core-test
  (:require-macros [cljs.test :refer [deftest testing is async]])
  (:require [cljs.test]
            [oska.core :as core]))

;[1 2 3]
;{:a 1 :b 2}
; #{1 2 3}

(defn- ->piece [player x y]
  {:player player :x x :y y})

(deftest won? []
  (testing "win for red"
    (is (core/won? :red [(->piece :red 0 0)])))
  (testing "not win for red"
    (is (not (core/won? :red [(->piece :red 1 1)]))))
  (testing "another win for red"
    (is (core/won? :red [(->piece :red 0 2)])))
  (testing "win for blue"
    (is (core/won? :blue [(->piece :blue 4 0)])))
  (testing "another not win for red"
    (is (not (core/won? :red [(->piece :red 0 2) (->piece :red 1 1)]))))
  (testing "win for red with blue still in play"
    (is (core/won? :red [(->piece :red 0 0) (->piece :blue 1 1)]))))

(deftest valid-move? []
  (testing "red can move to empty adjacent cell"
    (is (core/valid-move? [(->piece :red 1 1)] (->piece :red 1 1) [0 0])))
  (testing "red cannot move to non-adjacent cell"
    (is (not (core/valid-move? [(->piece :red 1 1)] (->piece :red 1 1) [0 4])))
    (is (not (core/valid-move? [(->piece :red 1 1)] (->piece :red 1 1) [1 3])))
    (is (not (core/valid-move? [(->piece :red 1 1)] (->piece :red 1 1) [3 1]))))
  (testing "cannot move to occupied cell"
    (is (not (core/valid-move? [(->piece :red 1 1) (->piece :blue 0 0)] (->piece :red 1 1) [0 0]))))
  (testing "cannot move to non-existent cell"
    (is (not (core/valid-move? [(->piece :red 1 1) (->piece :blue 0 0)] (->piece :red 1 1) [2 0]))))
  (testing "can jump"
    (is (core/valid-move? [(->piece :red 2 2) (->piece :blue 1 1)] (->piece :red 2 2) [0 0])))
  (testing "doesn't jump unless the other player is adjacent"
    (is (not (core/valid-move? [(->piece :red 2 2)] (->piece :red 2 2) [0 0])))))
