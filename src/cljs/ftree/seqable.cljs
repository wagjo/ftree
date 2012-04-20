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

(ns ftree.seqable
  "Seqable tree."
  (:require [ftree.tree :as ft]
            [ftree.protocol :as ftp]))

;;;; Implementation details

(deftype SeqableTree [tree]
  ISeqable
  (-seq [t] (when (not (ftp/-empty-tree? tree)) t))
  ISequential
  ISeq
  (-first [t] (ftp/-peekl t))
  (-rest [t] (ftp/-popl t))
  IPrintable
  (-pr-seq [t _] (concat ["("] (interpose " " (map pr-str t)) [")"]))
  #_(-pr-seq [_ _2] (list "#<SeqableFT " (pr-str tree) " >"))
  ftp/IEmptyTree
  (-empty-tree? [_] (ftp/-empty-tree? tree))
  ftp/IDeque
  (-conjl [_ o] (SeqableTree. (ftp/-conjl tree o)))
  (-conjr [_ o] (SeqableTree. (ftp/-conjr tree o)))
  (-peekl [_] (ftp/-peekl tree))
  (-peekr [_] (ftp/-peekr tree))
  (-popl [_] (SeqableTree. (ftp/-popl tree)))
  (-popr [_] (SeqableTree. (ftp/-popr tree)))
  ftp/IMeasured
  (-measured [_] (ftp/-measured tree))
  ftp/IMeasuredTree
  (-measured-popl [_] (ftp/-measured-popl tree))
  (-measured-popr [_] (ftp/-measured-popr tree))
  ftp/ISplittableTree
  (-split [_ pred acc] (let [[lt xs rt] (ftp/-split tree pred acc)]
                         [(SeqableTree. lt) xs (SeqableTree. rt)]))
  ftp/IConcatenableTree
  (-app3 [_ ts t2] (ftp/-app3 tree ts t2))
  (-app3deep [_ ts t1] (SeqableTree. (ftp/-app3deep tree ts t1))))

;;;; Public API

(defn create
  "Creates seqable finger tree.
  Returns create ftree."
  [& xs]
  (SeqableTree. (ft/to-tree xs)))
