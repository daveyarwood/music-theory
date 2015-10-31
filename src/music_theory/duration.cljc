(ns music-theory.duration)

(defn duration-ms
  "Given a number of beats and a tempo, calculates the duration in
   milliseconds."
  [beats tempo]
  (* beats (/ 60000.0 tempo)))
