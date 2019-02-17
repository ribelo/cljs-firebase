(ns ribelo.cljs-firebase.firestore
  (:require [clojure.string :as str]
            [oops.core :refer [oget ocall]]))


(def firebase (js/require "firebase"))


(defn firestore []
  (ocall firebase "firestore"))


(defn delete-field []
  (ocall firebase "firestore.FieldValue.delete"))


(defmulti path->field (fn [path] (type path)))


(defmethod path->field cljs.core/PersistentVector
  [path]
  (str/join "/" (map name path)))


(defmethod path->field cljs.core/Keyword
  [path]
  (name path))


(defmethod path->field js/String
  [path]
  path)


(defn doc->clj [doc]
  (js->clj (ocall doc "data")
           :keywordize-keys true))


(defn collection->clj [coll]
  (map doc->clj (oget coll "docs")))


(defn query->clj [coll]
  (collection->clj coll))


(defn on-doc-snapshot! [path f & {:keys [on-success on-failure]}]
  (-> (firestore)
      (ocall "doc" (path->field path))
      (ocall "onSnapshot" f)
      (cond-> on-success
              (ocall "then" on-success))
      (ocall "catch" (if on-failure on-failure #(js/Error %)))))


(defn on-coll-snapshot! [path f & {:keys [on-success on-failure]}]
  (-> (firestore)
      (ocall "collection" (path->field path))
      (ocall "onSnapshot" (fn [snap] (f (collection->clj snap))))
      (cond-> on-success (ocall "then" on-success))
      (ocall "catch" (if on-failure on-failure #(js/Error %)))))


(defn unsubscribe [path]
  (-> (firestore)
      (ocall "doc" (path->field path))
      (ocall "onSnapshot" (fn []))))


(defn unsubscribe-coll [path]
  (-> (firestore)
      (ocall "collection" (path->field path))
      (ocall "onSnapshot" (fn []))))

