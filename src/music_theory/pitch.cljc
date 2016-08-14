(ns music-theory.pitch
  #?(:cljs (:require-macros music-theory.pitch))
  (:require [music-theory.note          :as note]
            [music-theory.pitch.tunings :as tunings]))

(def ^:dynamic *reference-pitch* 440)
(def ^:dynamic *tuning-system* :equal)
(def ^:dynamic *tonic* nil)
(def ^:dynamic *scale-type* :major)

(defn set-reference-pitch!
  "Changes the reference pitch, which is the frequency of A4. (default: 440)"
  [freq]
  #?(:clj  (alter-var-root #'*reference-pitch* (constantly freq))
     :cljs (set! *reference-pitch* freq)))

#?(:clj
(defmacro with-reference-pitch
  "Executes the body, with *reference-pitch* bound to a given frequency."
  [freq & body]
  `(binding [*reference-pitch* ~freq]
     ~@body)))

(defn set-key!
  "Sets the key, which is required by some tuning systems in order to calculate
   the frequency of a note in Hz."
  ([tonic]
    (set-key! tonic :major))
  ([tonic scale-type]
    #?(:clj  (alter-var-root #'*tonic* (constantly tonic))
       :cljs (set! *tonic* tonic))
    #?(:clj  (alter-var-root #'*scale-type* (constantly scale-type))
       :cljs (set! *scale-type* scale-type))))

#?(:clj
(defmacro with-key
  "Executes the body, with *tonic* and *scale-type* bound to those provided."
  [tonic scale-type & body]
  `(binding [*tonic*      ~tonic
             *scale-type* ~scale-type]
     ~@body)))

(defn set-tuning-system!
  "Changes the tuning system. (default: :equal)"
  [system]
  #?(:clj  (alter-var-root #'*tuning-system* (constantly system))
     :cljs (set! *tuning-system* system)))

#?(:clj
(defmacro with-tuning-system
  "Executes the body, with *tuning-system* bound to a given tuning system.

   Some tuning systems need to be aware of what key you're in. This can be
   done via `set-key!` or via the `with-key` macro."
  [tuning & body]
  `(binding [*tuning-system* ~tuning]
     ~@body)))

#?(:clj
(defmacro with-tuning
  "Executes the body, binding *tuning-system* and/or *reference-pitch* to a
   particular tuning system and/or reference pitch.

   The first argument can be either a number (representing a reference pitch),
   a keyword (representing a tuning system), or a collection containing both."
  [x & body]
  `(let [[rp# ts#] (cond
                     (number? ~x)  [~x *tuning-system*]
                     (keyword? ~x) [*reference-pitch* ~x]
                     (coll? ~x)    [(or (first (filter number? ~x))
                                        *reference-pitch*)
                                    (or (first (filter keyword? ~x))
                                        *tuning-system*)])]
     (binding [*reference-pitch* rp#
               *tuning-system* ts#]
       ~@body))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn midi->hz
  "Converts a MIDI note (0-127) to its frequency in Hz.

   Reference pitch is A440 by default. To calculate pitch based on an alternate
   reference pitch (e.g. A430), set or bind *reference-pitch* to the frequency
   of A4.

   Tuning system is equal temperament by default. To calculate pitch based on
   an alternate tuning system (e.g. well temperament), set or bind
   *tuning-system* to a valid keyword representing that tuning system."
  [midi-note]
  {:pre  [(integer? midi-note)]
   :post [(not (neg? %))]}
  (case *tuning-system*
    :equal
    (tunings/equal-> *reference-pitch* midi-note)

    :pythagorean
    (tunings/pythagorean-> *reference-pitch* midi-note *tonic*)

    :mean ; (the default is the most common, 1/4 comma meantone)
    (tunings/quarter-comma-meantone-> *reference-pitch* midi-note *tonic*)
    :meantone
    (tunings/quarter-comma-meantone-> *reference-pitch* midi-note *tonic*)
    :quarter-comma-meantone
    (tunings/quarter-comma-meantone-> *reference-pitch* midi-note *tonic*)

    :well ; (the default is, somewhat arbitrarily, Werckmeister III)
    (tunings/werckmeister-iii-> *reference-pitch* midi-note *tonic*)
    :werckmeister
    (tunings/werckmeister-iii-> *reference-pitch* midi-note *tonic*)
    :werckmeister-iii
    (tunings/werckmeister-iii-> *reference-pitch* midi-note *tonic*)

    :just
    (tunings/just-> *reference-pitch* midi-note *tonic*)

    :young
    (tunings/young-> *reference-pitch* midi-note *tonic*)))

(defn note->hz
  "Converts a note in the form of a string or keyword (e.g. C#4, :Db5, A2) into
   its frequency in hz.

   Reference pitch is A440 by default. To calculate pitch based on an alternate
   reference pitch (e.g. A430), bind *reference-pitch* to the frequency of A4."
  [note]
  {:post [(not (neg? %))]}
  (-> (note/->note note) :number midi->hz))

(defn hz->midi
  "Converts a frequency in Hz to the closest MIDI note.

   Reference pitch is A440 by default. To calculate pitch based on an alternate
   reference pitch (e.g. A430), set or bind *reference-pitch* to the frequency
   of A4.

   Tuning system is equal temperament by default. To calculate pitch based on
   an alternate tuning system (e.g. well temperament), set or bind
   *tuning-system* to a valid keyword representing that tuning system."
  [freq]
  {:pre  [(number? freq) (pos? freq)]
   :post [(not (neg? %))]}
  (case *tuning-system*
    :equal
    (tunings/<-equal *reference-pitch* freq)))

