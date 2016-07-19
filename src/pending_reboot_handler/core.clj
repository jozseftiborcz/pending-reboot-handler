(ns pending-reboot-handler.core
  (:gen-class)
  (:require [clojure.string :as string]
            [clj-time.format :as tfmt]
            [clarango.core :as clcore]
            [clarango.document :as cldoc]
            [cheshire.generate :refer [add-encoder]]
            [clarango.collection :as clcol]))


"""This program implements the following protocol:
  0. only for computers needs reboot:
  1. Send warning emails to those whose computer needs reboot for more than 5 days and set warned-first-time.
  2. if warned-first-time and 2 days has passed warned-second-time
  3. if warned-second-time send forced-reboot email
  4. if forced-reboot-sent and 2 days has passed escalate."""

(add-encoder org.joda.time.DateTime 
             (fn [c jsonGenerator]
               (.writeString jsonGenerator (str c))))

(defn parse-command-line 
  [args]
  nil)

(defn hash-key-mapper 
  [field-name mapfn]
  (if (coll? field-name)
    (fn[h] (apply merge h (map #(hash-map % (mapfn (get h %))) field-name)))
    (fn[h] (merge h {field-name (mapfn (get h field-name))}))))

(defn read-reboot-log-file 
  [filename]
  (let [s (slurp (str "resources" "/" filename))
        lines (string/split s #"\r?\n")
        header (string/split (first lines) #"\t")
        lines (rest lines)
        bootup-conv (map (hash-key-mapper "LastBootUpTime" (fn[x] (tfmt/parse (tfmt/formatter "MM/dd/yyyy HH:mm:ss") x))))
        user-conv (map (hash-key-mapper "LoggedOnUser" (fn[x] (string/split x #";"))))
        TF-conv (map (hash-key-mapper ["CBS" "WSUS" "SCCM" "PendingFileRename"] (fn[x] (if (= x "True") true false))))]
    (sequence (comp TF-conv bootup-conv user-conv) (map #(zipmap header (string/split % #"\t")) lines))))

(defn partial-sort-by
  "Returns a partially sorted sequence of the items in coll, where the sort
    order is determined by comparing (keyfn item). If no comparator is
    supplied, uses compare. comparator can return nil which indicates ordering is 
    not defined between items. "
  ([coll]
  nil)
  ([keyfn coll]
   nil))

;(goal "Send warning emails to users")
(defn -main
  [& args]
  (parse-command-line args)
  ;(directory-source "resources")
  (let [lf (read-reboot-log-file "_Report.log")]
    (clcore/set-connection!)
    (clcore/set-default-db! "test1")
    ;(clcore/with-collection "test-collection" (cldoc/create-with-key {:reboots (read-reboot-log-file  "_Report.log")} :last-login-values)))
    (clcore/with-collection "test-collection" (cldoc/create {:reboots (read-reboot-log-file  "_Report.log")} )))
  (println "Hello, World!"))
