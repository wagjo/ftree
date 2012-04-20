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

(ns ftree.measure
  "Measures.")

;;;; Implementation details

;; Concrete measure type should be bound to these vars with
;; with-measure macro. Default implementation provides unmeasured
;; finger tree.
;;
;; This is due to the performance, as keeping reference to measure
;; object in each tree node is costly. Creating specific types of
;; nodes for different measures would be a mess (macros would have to
;; be used here extensively).

(def
  ^{:doc "Measure of an empty tree (identity element of a Monoid)."}
  -empty-tree-measure nil)

(def
  ^{:doc "Function which combines two measures (binary operation of
  a Monoid). Returns combined measure."}  
  -combine-measures-fn nil)

(def
  ^{:doc "Function which measures element e. Returns its measure."}  
  -measure-element-fn (fn [_] nil))

;;;; Public API

(defrecord Measure [empty-tree-measure
                    combine-measures-fn
                    measure-element-fn])

;; NOTE: with-measure macro defined in ftree/measure.clj

(defn set-measure!
  "Sets new measure. Returns the old one."
  [m]
  (let [old-measure (Measure. -empty-tree-measure
                              -combine-measures-fn
                              -measure-element-fn)]
    (set! -empty-tree-measure (:empty-tree-measure m))
    (set! -combine-measures-fn (:combine-measures-fn m))
    (set! -measure-element-fn (:measure-element-fn m))
    old-measure))
