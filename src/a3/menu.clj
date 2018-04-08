(ns a3.menu
  (:require [a3.db :as db]
            [a3.aggregations :as aggs]
            [a3.utils :refer [l]]))

(def greet-menu-items
  [[1 "Display Customer Table"   (fn [db] (doall (map vals (:cust-table db)))) false] ;; 'false' stands for 'has-args' and is fine
   [2 "Display Product Table"    (fn [db] (doall (map vals (:prod-table db)))) false]
   [3 "Display Sales Table"      aggs/sales false]
   [4 "Total Sales for Customer" (fn [db cust-name] [cust-name (aggs/total-cost-for-cust db {:name cust-name})]) true]
   [5 "Total Count for Product"  (fn [db prod-name] [prod-name (aggs/total-count-of-prod db {:itemDescription prod-name})]) true]])

(def greet-header
  "*** Sales Menu ***
  ------------------
")

(def greet-menu
  (str greet-header
       (apply str
              (map (fn [[menu-num menu-desc]]
                     (format "%s. %s\n" menu-num menu-desc))
                   greet-menu-items))))

(defn menu []
  (let [db (db/get-db)]
    (println greet-menu)
    (println "Describe your desires:")
    (try 
      (let [read (read-line)]
        (case read
          "6" (println "Bye!")
          (doseq [[id _ func has-arg] greet-menu-items]
            (when (= (str id) read)
              (println
               (if-let [arg (when has-arg
                              (println "We need some more for you:") ;; Terrible UX, no need to tell
                              (read-line))]
                 (func db arg)
                 (func db)))
              (menu)))))
      (catch Exception ex
        (println ex)
        (println "Umf... it went bad. Let's try again.\n")
        (menu)))))

(defn -main []
  (menu))
