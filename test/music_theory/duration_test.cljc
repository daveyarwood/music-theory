(ns music-theory.duration-test
  (:require #?(:clj  [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros (deftest testing is)])
            [music-theory.test-helpers :refer (=ish)]
            [music-theory.duration :as dur]))

(deftest duration-tests
  (testing "beats -> duration (ms) conversion"
    (is (=ish (dur/duration-ms 0.5 60)) 500)
    (is (=ish (dur/duration-ms 0.5 90)) 375)
    (is (=ish (dur/duration-ms 0.5 120)) 250)
    (is (=ish (dur/duration-ms 1 60) 1000))
    (is (=ish (dur/duration-ms 1 90) 666.666))
    (is (=ish (dur/duration-ms 1 120) 500))
    (is (=ish (dur/duration-ms 1.5 60)) 1500)
    (is (=ish (dur/duration-ms 1.5 90)) 1125)
    (is (=ish (dur/duration-ms 1.5 120) 750))
    (is (=ish (dur/duration-ms 2 60)) 2000)
    (is (=ish (dur/duration-ms 2 90)) 1500)
    (is (=ish (dur/duration-ms 2 120)) 1000)))
