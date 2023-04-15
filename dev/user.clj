(ns user
  (:require [clojure.tools.namespace.repl :as rr]))

(rr/set-refresh-dirs "src" "test")

(defn refresh-repl []
  (with-out-str (rr/refresh)))
