(ns music-theory.pitch-test
  (:require #?(:clj  [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros (deftest testing is)])
            [music-theory.test-helpers :refer (=ish round)]
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

(deftest key-tests
  (testing "set-key!"
    (pitch/set-key! :c# :major)
    (is (= pitch/*tonic* :c#))
    (is (= pitch/*scale-type* :major))
    (pitch/set-key! :d# :minor)
    (is (= pitch/*tonic* :d#))
    (is (= pitch/*scale-type* :minor)))
  (testing "with-key"
    (pitch/with-key :f# :major
      (is (= pitch/*tonic* :f#))
      (is (= pitch/*scale-type* :major)))))

(deftest reference-pitch-tests
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

(deftest tuning-tests
  (testing "equal temperament"
    (pitch/with-tuning-system :equal
      (let [expected '(261.6 277.2 293.7 311.1 329.6 349.2
                       370.0 392.0 415.3 440.0 466.2 493.9)
            actual   (map pitch/midi->hz (range 60 72))]
        (doseq [n (range 12)]
          (is (=ish (nth expected n) (round 1 (nth actual n))))))))
  (testing "Werckmeister III"
    (pitch/with-tuning-system :werckmeister-iii
      (pitch/with-key :c :major
        (let [expected '(261.6 275.6 292.3 310.1 327.8 348.8
                         367.5 391.1 413.4 437.0 465.1 491.7)
              actual   (map pitch/midi->hz (range 60 72))]
          (doseq [n (range 12)]
            (is (=ish (nth expected n) (round 1 (nth actual n)))))))))
  (testing "just temperament"
    (pitch/with-tuning-system :just
      (pitch/with-key :c :major
        (let [expected '(261.6 279.1 294.3 314.0 327.0 348.8
                         367.9 392.4 418.6 436.0 470.9 490.5)
              actual   (map pitch/midi->hz (range 60 72))]
          (doseq [n (range 12)]
            (is (=ish (nth expected n) (round 1 (nth actual n)))))))))
  (testing "La Monte Young's tuning in The Well-Tuned Piano"
    (pitch/with-tuning-system :young
      (pitch/with-key :c :major
        (let [expected '(261.6 289.7 294.3 300.5 343.4 338.0
                         386.3 392.4 400.6 457.8 450.7 515.1)
              actual   (map pitch/midi->hz (range 60 72))]
          (doseq [n (range 12)]
            (is (=ish (nth expected n) (round 1 (nth actual n))))))))))
