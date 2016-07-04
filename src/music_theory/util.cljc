(ns music-theory.util)

(defn error
  "Throws a Clojure or ClojureScript error/exception."
  [msg]
  (throw (new #?(:clj Exception :cljs js/Error) msg)))

(defn parse-int
  "Parses a string as an integer."
  [n]
  (#?(:clj Integer/parseInt :cljs js/Number) n))
