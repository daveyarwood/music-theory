(ns music-theory.pitch-test
  (:require #?(:clj  [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros (deftest testing is)])
            [music-theory.test-helpers :refer (=ish round)]
            [music-theory.pitch        :as    pitch]))

(deftest conversion-tests
  (testing "conversions:"
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
