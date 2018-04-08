(ns a3.db
  (:require [clojure.string :as str]
            [a3.utils :refer [l]]))

(def tables-info
  {:cust-table {:table-name :cust-table
                :path "./resources/my-cust.txt"
                :columns [:id :name :address :phone-number]}
   :prod-table {:table-name :prod-table
                :path "./resources/my-prod.txt"
                :columns [:id :itemDescription :unitCost]}
   :sales-table {:table-name :sales-table
                 :path "./resources/my-sales.txt"
                 :columns [:id :custID :prodID :itemCount]}})

(defn match-row [table partial-row]
  (filter (fn [row]
            (some (fn [[k v]] (= (get row k) v)) partial-row))
          table))

(defn parse-table [txt-table columns]
  (for [txt-row (str/split txt-table #"\n")]
    (->> (map (fn [row-el column-name] [row-el column-name])
              columns
              (str/split txt-row #"\|"))
         (into {}))))

(defn get-table [{:keys [table-name path columns]}]
  {table-name (parse-table (slurp path) columns)})

(defn get-db []
  (->> (map get-table (vals tables-info))
       (reduce merge)))

