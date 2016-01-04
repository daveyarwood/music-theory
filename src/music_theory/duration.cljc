(ns music-theory.duration)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; note lengths

; US
(def DOUBLE-WHOLE          0.5)
(def WHOLE                 1)
(def HALF                  2)
(def QUARTER               4)
(def EIGHTH                8)
(def SIXTEENTH             16)
(def THIRTY-SECOND         32)
(def SIXTY-FOURTH          64)
(def HUNDRED-TWENTY-EIGHTH 128)

; UK
(def BREVE                  0.5)
(def SEMIBREVE              1)
(def MINIM                  2)
(def CROTCHET               4)
(def QUAVER                 8)
(def SEMIQUAVER             16)
(def DEMISEMIQUAVER         32)
(def HEMIDEMISEMIQUAVER     64)
(def SEMIHEMIDEMISEMIQUAVER 128)

(defn dots
  "Adds `dots` dots to a note value, returning an 'adjusted' note value.

   NOTE: These note values are not expressed in beats, but rather in terms of
   the denominator of a fraction of a whole note, which is the system used to
   describe the base note values. In other words, the value of a quarter note
   is 4 because it equals one quarter (1/4) of a whole note. Mathematically,
   a single note in a quarter note triplet can be thought of as a 'sixth note'
   (with a note value of 6) because it equals 1/6 of a whole note.

   Dotted notes make for more complicated fractions. The value of a dotted
   quarter note is 2.666, or 8/3. The value of a double-dotted quarter note is
   2.2857..., or 16/7. As more dots are added, the value of the note becomes
   smaller, asymptotically approaching the next longer note length (which in
   this case is 2, a half note).

   (dots 0 QUARTER) = a quarter note (4)
   (dots 1 QUARTER) = a dotted quarter note (2.6666...)
   (dots 2 QUARTER) = a double-dotted quarter note (2.2857...)
   (dots 3 QUARTER) = a triple-dotted quarter note (2.1333...)

   The `beats` function can be used to convert these adjusted note values into
   the number of beats."
  [dots note-length]
  {:pre [(integer? dots)
         (not (neg? dots))
         (number? note-length)
         (pos? note-length)]}
  (let [numer (Math/pow 2 dots)
        denom (dec (* numer 2))
        ratio (/ numer denom)]
    (* note-length ratio)))

(defn ->note-length
  "Given a string representation of a note length, returns its numeric value.

   A string representation of a note length consists of a number representing a
   note value (e.g. '2' = a half note), followed by any number of dots.

   e.g.
   (->note-length '4') =>  4         (quarter note)
   (->note-length '4.') => 2.6666... (dotted quarter note)"
  [string]
  (let [[note-value ds] (rest (re-matches #"(\d+)(\.+)?" string))]
    (if note-value
      (let [note-value (#?(:clj  Integer/parseInt
                           :cljs js/Number)
                        note-value)
            ds (count (seq ds))]
        (dots ds note-value))
      (throw (new #?(:clj  Exception
                     :cljs js/Error)
                  "Invalid note-length format.")))))

(declare beats)
(defn note-length+
  "Adds together a variable number of note-lengths.

   e.g. 4 + 4 = 2    (quarter + quarter = half)"
  [& note-lengths]
  (/ 4.0 (apply beats note-lengths)))

(defn tuplet
  "Applies a tuplet ratio to each note length and returns the sum of the
   adjusted note lengths.

   For more information about tuplets:
   http://www2.siba.fi/muste1/index.php?id=100&la=en

   e.g.:
   - A single quarter note triplet is mathematically a 'sixth note', since six
     of them will fit into a whole note.
   - A single quarter note triplet therefore has a value of 6, mathematically
     speaking.
   - From a musician's perspective, a quarter note triplet (as in 3 notes) is
     three (3) quarter notes stuffed into the duration of a half note (2).
   - The ratio of this type of note is therefore 3/2.
   - Similarly, a 'duplet' (e.g. two eighth notes spread across a bar of 3/8)
     has a 2/3 ratio. In other words, 4 notes spread across a bar of 6/8 has a
     4/6 ratio, which is the same ratio, mathematically.

   NOTE: in ClojureScript, ratios must be expressed like (/ 2 3)

   (tuplet 2/3 4) => 6
   (tuplet 2/3 4 4 4) => 2"
  [ratio & note-lengths]
  (apply note-length+ (map (partial * ratio) note-lengths)))

(defn duplet
  "Applies the duplet ratio 2/3 to each note length and returns the sum of the
   adjusted note lengths."
  [& note-lengths]
  (apply tuplet #?(:clj 2/3 :cljs (/ 2 3)) note-lengths))

(defn triplet
  "Applies the triplet ratio 3/2 to each note length and returns the sum of the
   adjusted note lengths."
  [& note-lengths]
  (apply tuplet #?(:clj 3/2 :cljs (/ 3 2)) note-lengths))

(defn quadruplet
  "Applies the quadruplet ratio 4/3 to each note length and returns the sum of
   the adjusted note lengths."
  [& note-lengths]
  (apply tuplet #?(:clj 4/3 :cljs (/ 4 3)) note-lengths))

(defn quintuplet
  "Applies the quintuplet ratio 5/4 to each note length and returns the sum of
   the adjusted note lengths."
  [& note-lengths]
  (apply tuplet #?(:clj 5/4 :cljs (/ 5 4)) note-lengths))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; note lengths -> beats

(defn beats
  "Converts a note value to a number of beats.

   e.g.
   An eighth note has a note value of 8, and equals 0.5 beats.
   A quarter note has a note value of 4, and equals 1 beat.
   A half note has a note value of 2, and equals 2 beats.
   A whole note has a note value of 1, and equals 4 beats.

   When given more than one note value, adds them up and returns the sum.

   Note values can be provided as either numbers, note-length constants (which
   are numbers), or strings consisting of an integer note value followed by any
   number of dots.

   A collection of note values (e.g. a measure) is also an acceptable value.

   e.g.
   (beats 1 2 4 8)
   (beats WHOLE HALF QUARTER EIGHTH)
   (beats '1.' '2..' 4 SIXTEENTH)
   (beats [4 4 4 4])"
  [& note-values]
  (apply + (for [x note-values]
             (let [note-value (cond
                                (string? x) (->note-length x)
                                (number? x) x
                                (coll? x)   (apply note-length+ x)
                                :else (throw (new #?(:clj  Exception
                                                     :cljs js/Error)
                                                  "Invalid note-length(s).")))]
               (/ 4.0 note-value)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; beats -> duration (ms)

(defn duration-ms
  "Given a number of beats and a tempo, calculates the duration in
   milliseconds."
  [beats tempo]
  (* beats (/ 60000.0 tempo)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; measures

(defn measure?
  "Returns true if the provided note-lengths equal exactly one measure in the
   provided time-signature.

   `time-signature` is represented as a fraction, e.g. 4/4 (or (/ 4 4) in
   ClojureScript)

   `note-lengths` is a collection of note values, which may be represented as
   numbers (e.g. 4 is a quarter note), constants (e.g. EIGHTH), or strings
   consisting of a note value and any number of dots (e.g. '2..')

   (measure? 4/4 [4 4 '4..' 16]) => true"
  [time-signature note-lengths]
  (== (* 4 time-signature) (apply beats note-lengths)))
