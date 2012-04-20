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

(ns ftree.protocol
  "Ftree protocols."
  (:require [ftree.measure :as measure]))

;;;; Protocols

(defprotocol IDeque
  "Deque operations."
  (-conjl [d e] "Returns a new object of the same type with element e added to the left.")
  (-conjr [d e] "Returns a new object of the same type with element e added to the right.")
  (-peekl [d] "Returns leftmost element.")
  (-peekr [d] "Returns rightmost element.")
  (-popl [d] "Returns a new object of the same type without leftmost element.")
  (-popr [d] "Returns a new object of the same type without rightmost element."))

(defprotocol IMeasured
  "Something which can be measured."
  (-measured [t] "Returns measured value of t."))

(defprotocol IMeasuredTree
  "Measured operations."
  (-measured-popl [t] "Returns the measured value of t not including the leftmost item.")
  (-measured-popr [t] "Returns the measured value of t not including the rightmost item."))

(defprotocol ISplittableTree
  "Split operations."
  (-split [t pred-fn acc] "Returns [pre m post] where pre and post are finger trees."))

(defprotocol IConcatenableTree
  "Append operations."
  (-app3 [t1 ts t2] "Appends ts and (possibly deep) t2 to tree t1.")
  (-app3deep [t2 ts t1] "Appends ts and t2 to deep tree t1."))

(defprotocol IEmptyTree
  "Determine whether tree is empty."
  (-empty-tree? [t] "Returns true if t is empty tree, false otherwise."))

(defprotocol IInsertRemove
  "Support for text editor stuff."
  (-insert-before [t i xs] "Inserts xs seq before i position.")
  (-remove-at [t i] [t i count] "Removes count elements starting from i position.")
  (-replace-at [t i xs] [t i count xs] "Replaces count element starting from i position with xs seq.")
  (-update-at [t i update-fn args] "Updated count elements starting from i position with result of calling update-fn with old xs."))

(defprotocol ICountedDrop
  "Fast drop operations."
  (-dropl [t n] "Returns tree with all but first n leftmost items.")
  (-dropr [t n] "Returns tree with all but first n rightmost items."))

;;;; Implementations for base types

;; elements can be object, number, boolean, string and nil
(extend-type default
  IMeasured
  (-measured [o] (measure/-measure-element-fn o)))
