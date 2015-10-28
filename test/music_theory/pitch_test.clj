(ns music-theory.pitch-test
  (:require [clojure.test :refer :all]
            [music-theory.test-helpers :refer (=ish)]
            [music-theory.pitch :refer :all]))

(deftest note-tests
  (testing "representing notes as strings/keywords"
    (comment "->Note constructs a Note record, which represents a note as an
              unbounded MIDI note number.

              ->note is a helper function that creates a Note record from a
              string or keyword representing the note (letter + accidentals)
              and octave.")
    (is (= (->note "A-2")  (->Note -3)))
    (is (= (->note "Bb-2") (->Note -2)))
    (is (= (->note "B-2")  (->Note -1)))
    (is (= (->note "B#-2")  (->Note 0)))
    (is (= (->note "C-1")  (->Note 0)))
    (is (= (->note "C0")   (->Note 12)))
    (is (= (->note "C4")   (->Note 60)))
    (is (= (->note :C4)    (->Note 60)))
    (is (= (->note :Dbb4)  (->Note 60)))
    (is (= (->note "C#4")  (->Note 61)))
    (is (= (->note :C#4)   (->Note 61)))
    (is (= (->note "Db4")  (->Note 61)))
    (is (= (->note :D4)    (->Note 62)))
    (is (= (->note :D##4)  (->Note 64)))
    (is (= (->note "G9")   (->Note 127)))
    (is (= (->note "G#9")  (->Note 128)))
    (is (= (->note "A9")   (->Note 129)))))

(deftest conversion-tests
  (testing "conversions:"
    (testing "MIDI note -> frequency"
      (is (=ish (midi->hz 0) 8.176))       ; C-1
      (is (=ish (midi->hz 21) 27.5))       ; A0
      (is (=ish (midi->hz 27) 38.891))     ; Eb1
      (is (=ish (midi->hz 43) 97.999))     ; G2
      (is (=ish (midi->hz 57) 220))        ; A3
      (is (=ish (midi->hz 60) 261.63))     ; C4
      (is (=ish (midi->hz 69) 440))        ; A4
      (is (=ish (midi->hz 72) 523.25))     ; C5
      (is (=ish (midi->hz 73) 554.37))     ; C#5
      (is (=ish (midi->hz 74) 587.33))     ; D5
      (is (=ish (midi->hz 90) 1479.98))    ; F#6
      (is (=ish (midi->hz 100) 2637.02))   ; E7
      (is (=ish (midi->hz 108) 4186))      ; C8
      (is (=ish (midi->hz 125) 11175.3))   ; F9
      (is (=ish (midi->hz 127) 12543.85))) ; G9
    (testing "frequency -> the nearest MIDI note"
      (is (= (hz->midi 7.95) 0))
      (is (= (hz->midi 8.176) 0))
      (is (= (hz->midi 25.96) 20))
      (is (= (hz->midi 26) 20))
      (is (= (hz->midi 107.3) 45))
      (is (= (hz->midi 261) 60))
      (is (= (hz->midi 430) 69))
      (is (= (hz->midi 439) 69))
      (is (= (hz->midi 440) 69))
      (is (= (hz->midi 441) 69))
      (is (= (hz->midi 8372) 120)))
    (testing "note -> MIDI note"
      (is (= (note->midi "C-1") 0))
      (is (= (note->midi "A0") 21))
      (is (= (note->midi "Eb1") 27))
      (is (= (note->midi "Dbb4") 60))
      (is (= (note->midi "A4") 69)))
    (testing "note -> frequency"
      (is (=ish (note->hz "C-1") 8.176))
      (is (=ish (note->hz "A0") 27.5))
      (is (=ish (note->hz "Eb1") 38.891))
      (is (=ish (note->hz "Dbb4") 261.63))
      (is (=ish (note->hz "A4") 440)))))

(deftest tuning-tests
  (testing "tune!"
    (tune! 430)
    (is (=ish (note->hz "A4") 430))
    (is (=ish (note->hz "A3") 215))
    (is (=ish (note->hz "A5") 860))
    (tune! 432)
    (is (=ish (note->hz "A4") 432))
    (is (=ish (note->hz "A3") 216))
    (is (=ish (note->hz "A5") 864))
    (tune! 440))
  (testing "with-reference-pitch"
    (with-reference-pitch 430
      (is (=ish (note->hz "A4") 430))
      (is (=ish (note->hz "A3") 215))
      (is (=ish (note->hz "A5") 860)))
    (with-reference-pitch 432
      (is (=ish (note->hz "A4") 432))
      (is (=ish (note->hz "A3") 216))
      (is (=ish (note->hz "A5") 864)))
    (is (=ish (note->hz "A4") 440))
    (is (=ish (note->hz "A3") 220))
    (is (=ish (note->hz "A5") 880))))
