(ns music-theory.pitch-test
  (:require #?(:clj  [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros (deftest testing is)])
            [music-theory.test-helpers :refer (=ish)]
            [music-theory.note  :as note]
            [music-theory.pitch :as pitch]))

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
      (is (= (note/note->midi "A4") 69)))
    (testing "MIDI note -> frequency"
      (is (=ish (pitch/midi->hz 0) 8.176))       ; C-1
      (is (=ish (pitch/midi->hz 21) 27.5))       ; A0
      (is (=ish (pitch/midi->hz 27) 38.891))     ; Eb1
      (is (=ish (pitch/midi->hz 43) 97.999))     ; G2
      (is (=ish (pitch/midi->hz 57) 220))        ; A3
      (is (=ish (pitch/midi->hz 60) 261.63))     ; C4
      (is (=ish (pitch/midi->hz 69) 440))        ; A4
      (is (=ish (pitch/midi->hz 72) 523.25))     ; C5
      (is (=ish (pitch/midi->hz 73) 554.37))     ; C#5
      (is (=ish (pitch/midi->hz 74) 587.33))     ; D5
      (is (=ish (pitch/midi->hz 90) 1479.98))    ; F#6
      (is (=ish (pitch/midi->hz 100) 2637.02))   ; E7
      (is (=ish (pitch/midi->hz 108) 4186))      ; C8
      (is (=ish (pitch/midi->hz 125) 11175.3))   ; F9
      (is (=ish (pitch/midi->hz 127) 12543.85))) ; G9
    (testing "frequency -> the nearest MIDI note"
      (is (= (pitch/hz->midi 7.95) 0))
      (is (= (pitch/hz->midi 8.176) 0))
      (is (= (pitch/hz->midi 25.96) 20))
      (is (= (pitch/hz->midi 26) 20))
      (is (= (pitch/hz->midi 107.3) 45))
      (is (= (pitch/hz->midi 261) 60))
      (is (= (pitch/hz->midi 430) 69))
      (is (= (pitch/hz->midi 439) 69))
      (is (= (pitch/hz->midi 440) 69))
      (is (= (pitch/hz->midi 441) 69))
      (is (= (pitch/hz->midi 8372) 120)))
    (testing "note -> frequency"
      (is (=ish (pitch/note->hz "C-1") 8.176))
      (is (=ish (pitch/note->hz "A0") 27.5))
      (is (=ish (pitch/note->hz "Eb1") 38.891))
      (is (=ish (pitch/note->hz "Dbb4") 261.63))
      (is (=ish (pitch/note->hz "A4") 440)))))

(deftest key-tests
  (testing "set-key!"
    (pitch/set-key! :c# :major)
    (is (= pitch/*tonic* :c))
    (is (= pitch/*scale-type* :major))
    (pitch/set-key! :d# :minor)
    (is (= pitch/*tonic* :d))
    (is (= pitch/*scale-type* :minor)))
  (testing "with-key"
    (pitch/with-key :f# :major
      (is (= pitch/*tonic* :f#))
      (is (= pitch/*scale-type* :major)))))

(deftest tuning-tests
  (testing "set-reference-pitch!"
    (pitch/set-reference-pitch! 430)
    (is (=ish (pitch/note->hz "A4") 430))
    (is (=ish (pitch/note->hz "A3") 215))
    (is (=ish (pitch/note->hz "A5") 860))
    (pitch/set-reference-pitch! 432)
    (is (=ish (pitch/note->hz "A4") 432))
    (is (=ish (pitch/note->hz "A3") 216))
    (is (=ish (pitch/note->hz "A5") 864))
    (pitch/set-reference-pitch! 440))
  (testing "with-reference-pitch"
    (pitch/with-reference-pitch 430
      (is (=ish (pitch/note->hz "A4") 430))
      (is (=ish (pitch/note->hz "A3") 215))
      (is (=ish (pitch/note->hz "A5") 860)))
    (pitch/with-reference-pitch 432
      (is (=ish (pitch/note->hz "A4") 432))
      (is (=ish (pitch/note->hz "A3") 216))
      (is (=ish (pitch/note->hz "A5") 864)))
    (is (=ish (pitch/note->hz "A4") 440))
    (is (=ish (pitch/note->hz "A3") 220))
    (is (=ish (pitch/note->hz "A5") 880))))
