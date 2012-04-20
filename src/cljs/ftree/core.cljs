;; Copyright (c) Rich Hickey, Chris Houser. All rights reserved.
;; Copyright (C) 2012, Jozef Wagner. All rights reserved.
;;
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be
;; found in the file epl-v10.html at the root of this distribution.
;;
;; By using this software in any fashion, you are agreeing to be bound
;; by the terms of this license.
;;
;; You must not remove this notice, or any other, from this software.

(ns ^{:doc "Persistent collections based on 2-3 finger trees.

  Forked from https://github.com/clojure/data.finger-tree

  Create finger trees with following namespaces:
  - ftree.tree - bare tree
  - ftree.seqable - seqable tree
  - ftree.dl - double list
  - ftree.cdl - counted double list

  Resources on finger trees:
  - https://github.com/Chouser/talk-finger-tree
  - http://www.soi.city.ac.uk/~ross/papers/FingerTree.html
  - blip.tv/clojure/chris-houser-finger-trees-custom-persistent-collections-4632874"
      :author ["Chris Houser" "Jozef Wagner"]}
  ftree.core
  (:require [ftree.protocol :as ftp]
            [ftree.measure :as measure]))

;;;; Public API

;;; For any finger tree

(defn conjl
  "conj[oin] l[eft]. Returns a new finger tree with the xs 'added'
  to the left."
  ([coll x & xs]
     (if xs
       (recur (ftp/-conjl coll x) (first xs) (next xs))
       (ftp/-conjl coll x))))

(defn conjr
  "conj[oin] r[ight]. Returns a new finger tree with the xs
  'added' to the right."
  ([coll x & xs]
     (if xs
       (recur (ftp/-conjr coll x) (first xs) (next xs))
       (ftp/-conjr coll x))))

(defn peekl
  "Returns leftmost element."
  [coll]
  (ftp/-peekl coll))

(defn peekr
  "Returns rightmost element."
  [coll]
  (ftp/-peekr coll))

(defn popl
  "Returns a new finger tree without leftmost element."
  [coll]
  (ftp/-popl coll))

(defn popr
  "Returns a new finger tree without rightmost element."
  [coll]
  (ftp/-popr coll))

(defn concat-tree
  "Returns tree which is a concatenation of two given trees."
  [t1 t2]
  (ftp/-app3 t1 nil t2))

;;; Only for measured trees

(defn measured
  "Returns measure of object o."
  [o]
  (ftp/-measured o))

(defn split-tree
  "Returns result of splitting tree.
  Throws UnsupportedOperationException if not measured."
  [t p]
  (if measure/-combine-measures-fn
    (ftp/-split t p measure/-empty-tree-measure)
    (throw ex/unsupported-operation)))
