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

(defn tune!
  "Changes the reference pitch, which is the frequency of A4. (default: 440)"
  [freq]
  (alter-var-root #'*reference-pitch* (constantly freq)))

(defmacro with-reference-pitch
  "Executes the body, with *reference-pitch* bound to a given frequency."
  [freq & body]
  `(binding [*reference-pitch* ~freq]
     ~@body))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn midi->hz
  "Converts a MIDI note (0-127) to its frequency in Hz.

   Tuning is A440 by default. To calculate pitch based on an alternate
   reference pitch (e.g. A430), bind *reference-pitch* to the frequency of A4."
  [midi-note]
  {:pre  [(integer? midi-note)]
   :post [(not (neg? %))]}
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

(defn note->midi
  "Converts a note in the form of a string or keyword (e.g. C#4, :Db5, A2) into
   the corresponding MIDI note number.

   Throws an assertion error if the note is outside the range of MIDI notes
   (0-127)."
  [note]
  {:post [(<= 0 % 127)]}
  (:number (->note note)))

(defn note->hz
  "Converts a note in the form of a string or keyword (e.g. C#4, :Db5, A2) into
   its frequency in hz.

   Tuning is A440 by default. To calculate pitch based on an alternate
   reference pitch (e.g. A430), bind *reference-pitch* to the frequency of A4."
  [note]
  {:post [(not (neg? %))]}
  (-> (->note note) :number midi->hz))
