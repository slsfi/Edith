(ns edith.core)

(import 'com.google.inject.Guice)
(import 'com.mysema.edith.guice.ServiceModule)
(import 'com.mysema.edith.guice.SecurityModule)
(import 'com.mysema.edith.services.NoteDaoImpl)
(import 'com.mysema.edith.services.UserDaoImpl)
(import 'com.mysema.edith.EDITH)
(import 'org.tmatesoft.svn.core.SVNURL)
(import 'com.mysema.edith.domain.Note)

(def repository
  (java.io.File. "target/repo"))

(System/setProperty "production.mode" "true")
(System/setProperty EDITH/REPO_FILE_PROPERTY (.getAbsolutePath repository))
(System/setProperty EDITH/REPO_URL_PROPERTY (.toString (SVNURL/fromFile repository)))
(System/setProperty EDITH/EXTENDED_TERM "false")

(def injector
  (Guice/createInjector [(ServiceModule.) (SecurityModule.)]))

(def note-dao (.getInstance injector NoteDaoImpl))

(def user-dao (.getInstance injector UserDaoImpl))

(def note
  (doto (Note.)
    (.setLemma "huihai nyt mennään")
    (.setDescription "hienoin lemma ikinä")))

(.save note-dao note)

(.setLemma note "i haz been changed")

