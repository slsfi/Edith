;
; Copyright (c) 2018 Mysema
;

(ns edith.core
  (:require [clojure.java.io :as io])
  (:import com.google.inject.Guice
           [com.mysema.edith.guice ServiceModule SecurityModule]
           [com.mysema.edith.services NoteDaoImpl UserDaoImpl TermDaoImpl]
           com.mysema.edith.EDITH
           org.tmatesoft.svn.core.SVNURL
           [com.mysema.edith.domain Note Term]))

(def repository
  (java.io.File. "target/repo"))

(System/setProperty "production.mode" "true")
(System/setProperty EDITH/REPO_FILE_PROPERTY (.getAbsolutePath repository))
(System/setProperty EDITH/REPO_URL_PROPERTY (.toString (SVNURL/fromFile repository)))
(System/setProperty EDITH/EXTENDED_TERM "false")

(with-open [rdr (io/reader (.getResourceAsStream ServiceModule "/edith.properties"))]
  (doseq [line (line-seq rdr)]
    (println line)))

(def injector
  (Guice/createInjector [(ServiceModule.) (SecurityModule.)]))

(def note-dao (.getInstance injector NoteDaoImpl))

(def user-dao (.getInstance injector UserDaoImpl))

(def term-dao (.getInstance injector TermDaoImpl))

(defn find-note
  [id]
  (.getById note-dao id))

(defn save-note!
  [note]
  (.save note-dao note)) 

(comment
  (def note
    (find-note 19))

  (.setDescription note "magic man")

  (save-note! note)

  (.setTerm note (Term.))

  (.save term-dao (.getTerm note))

  (.getId (.getTerm note))

  (.setMeaning (.getTerm note) "sitä poikaa ei enää olekaan")

  (save-note! note)

  )

