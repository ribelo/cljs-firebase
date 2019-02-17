(ns ribelo.cljs-firebase.core)


(def firebase (js/require "firebase"))


(defn firestore []
  (ocall firebase "firestore"))


(defn initialize-app [firebase-app-info]
  (ocall firebase "initializeApp" (clj->js firebase-app-info)))
