(ns music-theory.chord-test
  (:require #?(:clj  [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros (deftest testing is)])
            [music-theory.chord :as chord]))

(deftest octave-span-tests
  (testing "determining the octave span of a chord"
    (is (= 1 (chord/octave-span [:D4 :F#4 :A4])))
    (is (= 1 (chord/octave-span [:D4 :F#4 :A4 :C#5])))
    (is (= 2 (chord/octave-span [:D4 :F#4 :A4 :D5])))
    (is (= 2 (chord/octave-span [:D4 :F#4 :A4 :C#5 :E5])))
    (is (= 3 (chord/octave-span [:C4 :C5 :E5 :G5 :C6])))))

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
  (testing "building a chord from a root note and a chord symbol"
    (is (= [:C4 :E4  :G4]            (chord/build-chord :C4 :C)))
    (is (= [:C4 :E4  :G4]            (chord/build-chord :C4 :CM)))
    (is (= [:C4 :E4  :G4]            (chord/build-chord :C4 :Cmaj)))
    (is (= [:B4 :D5  :F#5]           (chord/build-chord :B4 :Bm)))
    (is (= [:B4 :D5  :F#5]           (chord/build-chord :B4 :Bmin)))
    (is (= [:D4 :F#4 :A4  :C#5]      (chord/build-chord :D4 :Dmaj7)))
    (is (= [:A2 :C3  :Eb3 :Gb3]      (chord/build-chord :A2 :Adim7)))
    (is (= [:A2 :C3  :Eb3 :Gb3]      (chord/build-chord :A2 :A°7)))
    (is (= [:C3 :E3  :G#3 :Bb3]      (chord/build-chord :C3 :C+7)))
    (is (= [:C3 :Eb3 :Gb3 :Bbb3 :D4] (chord/build-chord :C3 :Cdim9))))
  (testing "building an inverted chord from a bass note and a chord symbol"
    (is (= [:E4 :G4  :C5]            (chord/build-chord :E4  :C)))
    (is (= [:G4 :C5  :E5]            (chord/build-chord :G4  :CM)))
    (is (= [:E3 :G3  :C4]            (chord/build-chord :E3  :Cmaj)))
    (is (= [:D4 :F#4 :B4]            (chord/build-chord :D4  :Bm)))
    (is (= [:F#4 :B4 :D5]            (chord/build-chord :F#4 :Bmin)))
    (is (= [:C#4 :D4 :F#4 :A4]       (chord/build-chord :C#4 :Dmaj7)))
    (is (= [:Eb3 :Gb3 :A3 :C4]       (chord/build-chord :Eb3 :Adim7)))
    (is (= [:C3  :Eb3 :Gb3 :A3]      (chord/build-chord :C3  :A°7)))
    (is (= [:Bb3 :C4 :E4 :G#4]       (chord/build-chord :Bb3 :C+7)))
    (is (= [:D2 :C3 :Eb3 :Gb3 :Bbb3] (chord/build-chord :D2  :Cdim9)))))

