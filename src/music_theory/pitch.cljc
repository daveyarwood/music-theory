(ns music-theory.pitch)

(defrecord Note [number])

(def ^:private intervals
  {"C" 0, "D" 2, "E" 4, "F" 5, "G" 7, "A" 9, "B" 11})

(defn ->note
  "Creates a Note record, which represents a note as an unbounded MIDI note
   number, from a string or keyword describing the note in scientific pitch
   notation, i.e. a letter and (optionally) any number of sharps and flats.

   e.g. C#5, Dbb4, E0"
  [x]
  (let [s (name x)
        [letter accs octave] (rest (re-matches #"([A-G])([#b]*)(-?\d+)" s))]
    (if (and letter accs octave)
      (let [octave (#?(:clj  Integer/parseInt
                       :cljs js/Number)
                    octave)
            base-note (+ (intervals letter) (* octave 12) 12)]
        (->Note (reduce (fn [note-number accidental]
                          (case accidental
                            \# (inc note-number)
                            \b (dec note-number)))
                        base-note
                        accs)))
      (throw (new #?(:clj  Exception
                     :cljs js/Error)
                  "Invalid note format.")))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def ^:dynamic *reference-pitch* 440)

(defn midi->hz
  "Converts a MIDI note (0-127) to its frequency in Hz.

   Tuning is A440 by default. To calculate pitch based on an alternate
   reference pitch (e.g. A430), bind *reference-pitch* to the frequency of A4."
  [midi-note]
  {:pre [(integer? midi-note)
         (<= 0 midi-note 127)]}
  (* *reference-pitch* (Math/pow 2 (/ (- midi-note 69) 12.0))))

(defn hz->midi
  "Converts a frequency in Hz to the closest MIDI note.

   Tuning is A440 by default. To calculate pitch based on an alternate
   reference pitch (e.g. A430), bind *reference-pitch* to the frequency of A4."
  [freq]
  {:pre  [(number? freq) (pos? freq)]
   :post [(not (neg? %))]}
  (letfn [(log2 [n] (/ (Math/log n) (Math/log 2)))]
    (Math/round (+ 69 (* 12 (log2 (/ freq *reference-pitch*)))))))
