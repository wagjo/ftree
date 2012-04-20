(defproject ftree "0.1.0-SNAPSHOT"
  :description "Finger trees in Clojurescript."
  :url "https://github.com/wagjo/ftree"
  :dependencies [[org.clojure/clojure "1.3.0"]]
  :checksum-deps false
  :disable-deps-clean true
  :source-path "src/clj"
;;  :extra-classpath-dirs ["c:\\dev\\clojurescript\\src\\cljs" "c:\\dev\\clojurescript\\src\\clj" "src\\cljs"]
  :extra-classpath-dirs ["clojurescript/src/cljs" "clojurescript/src/clj" "src/cljs"]
  :extra-files-to-clean [".out" ".repl" ".emacs.log" "web/js" "web/ftree.js" "web/ftree-dev.js"]
  :jvm-opts ["-Dswank.encoding=utf-8"]
  :warn-on-reflection true)

;;; you should install swank-clojure locally:
;; lein plugin install swank-clojure 1.3.2
