(set-env!
  :source-paths #{"src" "test"}
  :dependencies '[[org.clojure/clojure "1.7.0"]
                  [adzerk/bootlaces    "0.1.12" :scope "test"]
                  [adzerk/boot-test    "1.0.4"  :scope "test"]
                  [crisptrutski/boot-cljs-test "0.2.0-SNAPSHOT" :scope "test"]])

(require '[adzerk.bootlaces :refer :all]
         '[adzerk.boot-test :refer :all]
         '[crisptrutski.boot-cljs-test :refer (test-cljs)])

(def +version+ "0.1.0")

(bootlaces! +version+)

(task-options!
  pom {:project 'music-theory
       :version +version+
       :description "A music theory library for Clojure/ClojureScript"
       :url "https://github.com/daveyarwood/music-theory"
       :scm {:url "https://github.com/daveyarwood/music-theory"}
       :license {"name" "Eclipse Public License"
                 "url" "http://www.eclipse.org/legal/epl-v10.html"}}
  test {:namespaces '#{music-theory.pitch-test
                       music-theory.duration-test}})

(deftask space
  []
  (with-pre-wrap fs
    (println)
    fs))

(deftask print-heading
  [H heading HEADING str "The heading to print."]
  (with-pre-wrap fs
    (println "---" heading "---")
    fs))

(deftask tests
  "Runs tests for both Clojure and ClojureScript."
  []
  (comp
   (print-heading :heading "Clojure tests")
   (test)
   (space)
   (print-heading :heading "ClojureScript tests")
   (space)
   (test-cljs)))

(deftask deploy
  "Builds uberjar, installs it to local Maven repo, and deploys it to Clojars."
  []
  (comp
    (build-jar)
    (push-release)))
