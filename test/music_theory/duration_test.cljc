(ns music-theory.duration-test
  (:require #?(:clj  [clojure.test :refer :all]
               :cljs [cljs.test :refer-macros (deftest testing is)])
            [music-theory.test-helpers :refer (=ish)]
            [music-theory.duration :as dur]))

(deftest note-length-tests
  (testing "note lengths (US)"
    (is (= dur/DOUBLE-WHOLE          0.5))
    (is (= dur/WHOLE                 1))
    (is (= dur/HALF                  2))
    (is (= dur/QUARTER               4))
    (is (= dur/EIGHTH                8))
    (is (= dur/SIXTEENTH             16))
    (is (= dur/THIRTY-SECOND         32))
    (is (= dur/SIXTY-FOURTH          64))
    (is (= dur/HUNDRED-TWENTY-EIGHTH 128)))
  (testing "note lengths (UK)"
    (is (= dur/BREVE                  0.5))
    (is (= dur/SEMIBREVE              1))
    (is (= dur/MINIM                  2))
    (is (= dur/CROTCHET               4))
    (is (= dur/QUAVER                 8))
    (is (= dur/SEMIQUAVER             16))
    (is (= dur/DEMISEMIQUAVER         32))
    (is (= dur/HEMIDEMISEMIQUAVER     64))
    (is (= dur/SEMIHEMIDEMISEMIQUAVER 128))))

(deftest note-length-addition-tests
  (testing "adding together note lengths"
    (is (=ish (dur/note-length+ 4 4) 2))
    (is (=ish (dur/note-length+ 4 8) 2.666))
    (is (=ish (dur/note-length+ 4 4 4) 1.333))
    (is (=ish (dur/note-length+ 2 4 8 8) 1))))

(deftest dot-tests
  (testing "dotted note lengths"
    (is (=ish (dur/dots 0 dur/HALF) 2))
    (is (=ish (dur/dots 1 dur/HALF) 1.333 (/ 4 3)))
    (is (=ish (dur/dots 2 dur/HALF) 1.143 (/ 8 7)))
    (is (=ish (dur/dots 3 dur/HALF) 1.067 (/ 16 15)))
    (is (=ish (dur/dots 4 dur/HALF) 1.032 (/ 32 31)))
    (is (=ish (dur/dots 0 dur/QUARTER) 4))
    (is (=ish (dur/dots 1 dur/QUARTER) 2.666 (/ 8 3)))
    (is (=ish (dur/dots 2 dur/QUARTER) 2.286 (/ 16 7)))
    (is (=ish (dur/dots 3 dur/QUARTER) 2.133 (/ 32 15)))
    (is (=ish (dur/dots 4 dur/QUARTER) 2.065 (/ 64 31)))))

(deftest string-shorthand-test
  (testing "string shorthand for note lengths"
    (is (=ish (dur/->note-length "4")    dur/QUARTER))
    (is (=ish (dur/->note-length "2")    dur/HALF))
    (is (=ish (dur/->note-length "2.")   (dur/dots 1 dur/HALF)))
    (is (=ish (dur/->note-length "2..")  (dur/dots 2 dur/HALF)))
    (is (=ish (dur/->note-length "2...") (dur/dots 3 dur/HALF)))))

(deftest beats-test
  (testing "note value(s) -> beats conversion"
    (is (=ish (dur/beats dur/QUARTER) 1))
    (is (=ish (dur/beats (dur/dots 1 dur/QUARTER)) 1.5))
    (is (=ish (dur/beats "4.") 1.5))
    (is (=ish (dur/beats (dur/dots 2 dur/QUARTER)) 1.75))
    (is (=ish (dur/beats "4..") 1.75))
    (is (=ish (dur/beats (dur/dots 3 dur/QUARTER)) 1.875))
    (is (=ish (dur/beats "4...") 1.875))
    (is (=ish (dur/beats (dur/dots 1 dur/QUARTER)
                         (dur/dots 1 dur/QUARTER)) 3))
    (is (=ish (dur/beats "4." "4.") 3))
    (is (=ish (dur/beats "4." (dur/dots 1 dur/QUARTER)) 3))
    (is (=ish (dur/beats (dur/dots 1 dur/QUARTER) "4.") 3))
    (is (=ish (dur/beats dur/WHOLE dur/HALF dur/QUARTER) 7))
    (is (=ish (dur/beats "1" "2" "4") 7))
    (is (=ish (dur/beats "1" "2" "2") 8))
    (is (=ish (dur/beats "1" "2" "2.") 9))
    (is (=ish (dur/beats [4 4 4 4]) 4))
    (is (=ish (dur/beats ["2." 8]) 3.5))))

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

(deftest measure-tests
  (testing "measure length validation"
    (is (dur/measure? #?(:clj 4/4 :cljs (/ 4 4)) [dur/WHOLE]))
    (is (dur/measure? #?(:clj 4/4 :cljs (/ 4 4)) [4 4 4 4]))
    (is (dur/measure? #?(:clj 4/4 :cljs (/ 4 4)) [4 4 "4." 8]))
    (is (dur/measure? #?(:clj 3/8 :cljs (/ 3 8)) [8 8 8]))
    (is (dur/measure? #?(:clj 3/8 :cljs (/ 3 8)) [dur/QUARTER dur/EIGHTH]))))
