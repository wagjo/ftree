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

(ns ftree.exception
  "Exception types used in ftree.")

;; TODO: are there some basic sets of exceptions in ClojureScript?
(def unsupported-operation "Unsupported operation.")

(def illegal-argument "Illegal Argument.")

(def index-out-of-bounds "Index out of bounds.")
