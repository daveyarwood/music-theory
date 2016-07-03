(ns music-theory.chord-test
  (:require #?(:clj  [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros (deftest testing is)])
            [music-theory.chord :as chord]))

(deftest build-chord-tests
  (testing "building a chord from intervals"
    (is (= [:C4 :E4  :G4]
           (chord/build-chord :C4 [:M3 :m3])))
    (is (= [:D4 :F#4 :A4  :C#5]
           (chord/build-chord :D4 [:M3 :m3 :M3])))
    (is (= [:A2 :C3  :Eb3 :Gb3]
           (chord/build-chord :A2 [:m3 :m3 :m3])))
    (is (= [:G3 :G4  :D5  :B5]
           (chord/build-chord :G3 [:P8 :P5 :M6])))
    (is (= [:C3 :E3  :G#3 :Bb3]
           (chord/build-chord :C3 [:M3 :M3 :d3])))
    (is (= [:C3 :Eb3 :Gb3 :Bbb3 :D4]
           (chord/build-chord :C3 [:m3 :m3 :m3 :A3]))))
  (testing "building a chord from a base note and a chord symbol"
    (is (= [:C4 :E4  :G4]            (chord/build-chord :C4 :C)))
    (is (= [:C4 :E4  :G4]            (chord/build-chord :C4 :CM)))
    (is (= [:C4 :E4  :G4]            (chord/build-chord :C4 :Cmaj)))
    (is (= [:B4 :D5  :F#5]           (chord/build-chord :B4 :Bm)))
    (is (= [:B4 :D5  :F#5]           (chord/build-chord :B4 :Bmin)))
    (is (= [:D4 :F#4 :A4  :C#5]      (chord/build-chord :D4 :Dmaj7)))
    (is (= [:A2 :C3  :Eb3 :Gb3]      (chord/build-chord :A2 :Adim7)))
    (is (= [:A2 :C3  :Eb3 :Gb3]      (chord/build-chord :A2 :A°7)))
    (is (= [:C3 :E3  :G#3 :Bb3]      (chord/build-chord :C3 :C+7)))
    (is (= [:C3 :Eb3 :Gb3 :Bbb3 :D4] (chord/build-chord :C3 :Cdim9)))))


