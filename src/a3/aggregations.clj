(ns a3.aggregations
  (:require [a3.db :as db]
            [a3.utils :refer [l]]))

(defn sales [db]
  (let [{:keys [cust-table prod-table sales-table]} db]
    (doall
     (map (fn [{:keys [custID prodID itemCount]}]
            [(-> (db/match-row cust-table {:id custID}) first :name)
             (some #(when (= (:id %) prodID) (:itemDescription %)) prod-table)
             itemCount])
          sales-table))))

(defn total-cost-for-cust [db partial-cust]
  (let [{:keys [cust-table prod-table sales-table]} db
        cust (l (first (db/match-row cust-table partial-cust)))
        total (->> (db/match-row sales-table {:custID (:id cust)})
                   (map (fn [sale]
                          (let [prod (first (db/match-row prod-table {:id (:prodID sale)}))]
                            (* (Double/parseDouble (:unitCost prod)) ;; TODO: Add coercion in table spec
                               (Integer/parseInt (:itemCount sale))))))
                   (reduce +))]
    total))

(defn total-count-of-prod [db partial-prod]
  (let [{:keys [prod-table sales-table]} db
        prod (first (db/match-row prod-table partial-prod))
        total (->> (db/match-row sales-table {:prodID (:id prod)})
                   (map #(Integer/parseInt (:itemCount %)))
                   (reduce +))]
    total))
