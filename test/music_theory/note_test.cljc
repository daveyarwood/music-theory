(ns music-theory.note-test
  (:require #?(:clj  [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros (deftest testing is)])
            [music-theory.note :as note]))

(deftest note-tests
  (testing "representing notes as strings/keywords"
    (comment "->Note constructs a Note record, which represents a note as an
              unbounded MIDI note number.

              ->note is a helper function that creates a Note record from a
              string or keyword representing the note (letter + accidentals)
              and octave.")
    (is (= (note/->note "A-2")  (note/->Note -3)))
    (is (= (note/->note "Bb-2") (note/->Note -2)))
    (is (= (note/->note "B-2")  (note/->Note -1)))
    (is (= (note/->note "B#-2") (note/->Note 0)))
    (is (= (note/->note "C-1")  (note/->Note 0)))
    (is (= (note/->note "C0")   (note/->Note 12)))
    (is (= (note/->note "C4")   (note/->Note 60)))
    (is (= (note/->note :C4)    (note/->Note 60)))
    (is (= (note/->note :Dbb4)  (note/->Note 60)))
    (is (= (note/->note "C#4")  (note/->Note 61)))
    (is (= (note/->note :C#4)   (note/->Note 61)))
    (is (= (note/->note "Db4")  (note/->Note 61)))
    (is (= (note/->note :D4)    (note/->Note 62)))
    (is (= (note/->note :D##4)  (note/->Note 64)))
    (is (= (note/->note "G9")   (note/->Note 127)))
    (is (= (note/->note "G#9")  (note/->Note 128)))
    (is (= (note/->note "A9")   (note/->Note 129)))))

(deftest conversion-tests
  (testing "conversions:"
    (testing "note -> MIDI note"
      (is (= (note/note->midi "C-1") 0))
      (is (= (note/note->midi "A0") 21))
      (is (= (note/note->midi "Eb1") 27))
      (is (= (note/note->midi "Dbb4") 60))
      (is (= (note/note->midi "A4") 69)))))

(deftest octave-tests
  (testing "octaves:"
    (is (= (note/octave :Cb-1) -2))
    (is (= (note/octave :C-1) -1))
    (is (= (note/octave :C#-1) -1))
    (is (= (note/octave :C0) 0))
    (is (= (note/octave :Db0) 0))
    (is (= (note/octave :B0) 0))
    (is (= (note/octave :C1) 1))
    (is (= (note/octave :C2) 2))
    (is (= (note/octave :C#2) 2))
    (is (= (note/octave :G2) 2))))

(deftest interval-tests
  (testing "adding intervals to MIDI note numbers"
    (is (= 65 (note/interval+ 60 :P4)))
    (is (= 63 (note/interval+ 60 :m3)))
    (is (= 72 (note/interval+ 60 :P8)))
    (is (= 69 (note/interval+ 60 :M6)))
    (is (= 70 (note/interval+ 60 :P4 :P4)))
    (is (= 77 (note/interval+ 60 :P4 :P4 :P5))))
  (testing "adding intervals to notes with enharmonic spelling"
    (is (= :C4   (note/interval+ :B3  :m2)))
    (is (= :C#4  (note/interval+ :B3  :M2)))
    (is (= :Db4  (note/interval+ :Cb4 :M2)))
    (is (= :C4   (note/interval+ :C4  :P1)))
    (is (= :Db4  (note/interval+ :C4  :m2)))
    (is (= :D4   (note/interval+ :C4  :M2)))
    (is (= :Eb4  (note/interval+ :C4  :m3)))
    (is (= :E4   (note/interval+ :C4  :M3)))
    (is (= :F4   (note/interval+ :C4  :P4)))
    (is (= :F#4  (note/interval+ :C4  :A4)))
    (is (= :Gb4  (note/interval+ :C4  :d5)))
    (is (= :G4   (note/interval+ :C4  :P5)))
    (is (= :Ab4  (note/interval+ :C4  :m6)))
    (is (= :A4   (note/interval+ :C4  :M6)))
    (is (= :Bb4  (note/interval+ :C4  :m7)))
    (is (= :B4   (note/interval+ :C4  :M7)))
    (is (= :C5   (note/interval+ :C4  :P8)))
    (is (= :Db5  (note/interval+ :C4  :m9)))
    (is (= :D5   (note/interval+ :B4  :m3)))
    (is (= :D#5  (note/interval+ :B4  :M3)))
    (is (= :Eb5  (note/interval+ :C5  :m3)))
    (is (= :Eb5  (note/interval+ :Cb5 :M3)))
    (is (= :Eb5  (note/interval+ :Eb4 :P8)))
    (is (= :D5   (note/interval+ :C#4 :m9)))
    (is (= :D#5  (note/interval+ :C#4 :M9)))
    (is (= :E5   (note/interval+ :C#4 :m10)))
    (is (= :E#5  (note/interval+ :C#4 :M10)))
    (is (= :F#5  (note/interval+ :C#4 :P11)))
    (is (= :F##5 (note/interval+ :C#4 :A11)))
    (is (= :G5   (note/interval+ :C#4 :d12)))
    (is (= :G#5  (note/interval+ :C#4 :P12)))
    (is (= :A5   (note/interval+ :C#4 :m13)))
    (is (= :A#5  (note/interval+ :C#4 :M13)))
    (is (= :B5   (note/interval+ :C#4 :m14)))
    (is (= :B#5  (note/interval+ :C#4 :M14)))
    (is (= :C#6  (note/interval+ :C#4 :P15)))))
