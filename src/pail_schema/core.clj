(ns pail-schema.core
  "Defines Pail-Schema core functionality.
   clj-pail-tap.core and pail-cascalog.core are integrated
   into this namespace."
  (:require [potemkin :as pt]
            [schema.core :as s]
            [clj-pail-tap.core]
            [pail-cascalog.core])
  (:import (com.backtype.hadoop.pail Pail PailSpec PailStructure)))

; pull in clj-pail and pail-cascalog core functionality.
(pt/import-vars [clj-pail-tap.core
                 list-taps
                 tap-map
                 object-seq
                 spec
                 pail
                 with-snapshot
                 create
                 find-or-create
                 absorb
                 snapshot
                 delete-snapshot
                 copy-append
                 move-append
                 consolidate
                 delete
                 pail-is-empty?
                 pail-exists?
                 write-objects]
                [pail-cascalog.core
                 pail->tap
                 tap-options
                 tap])

(defn writer [pail]
  (.openWrite pail))

(defn close [writer]
  (.close writer))

(defn write [writer o]
  (.writeObject writer o))

(defn get-tap
  "Creates a `PailTap` from an existing vertically partitioned pail, by selecting an
   entry from the Pail's tap map. Takes a pail connection. returns nil if no tap found."
  [pail tap-key]
  (when-let [attrs (tap-key (tap-map pail))]
    (pail->tap pail :field-name (name tap-key)
               :attributes [attrs])))

; The rest is a descent parser to get property paths from a schema.
; This is not complete, but it works for simple schemas.

(declare get-field-keys)

(defn- keys-for-either [schema]
  (mapcat get-field-keys (:schemas schema)))

(defn- key-keyword [keyish]
  (condp = (type keyish)
    schema.core.OptionalKey (:k keyish)
    keyish))

(defn- keys-for-map [schema]
  (map vector
       (map key-keyword (keys schema))
       (map get-field-keys (vals schema))))

(defn- get-field-keys [schema]
  (condp = (type schema)
    schema.core.Either (keys-for-either schema)
    clojure.lang.PersistentArrayMap (keys-for-map schema)
    []))


(defn- node? [t]
  (and (keyword? (first t))
       (= 2 (count t))))

(defn- last-node? [t]
  (and (node? t)
       (= [] (last t))))

(defn- get-property-paths [tree]
  (cond
   (last-node? tree) [ [(first tree)] ]
   (node? tree)
   (let [children (get-property-paths (last tree))]
     (map #(conj % (first tree)) children))
   (seq? tree)
     (mapcat get-property-paths tree)))


(defn property-paths
  "Get a list of property paths for a thrift data type.
   Each row consists of a set of field maps leading
   to a field, such that a path can be created for a property similar
   to the way a Pail Partitioner does. Using field ids or field names."
  [schema]
  (map reverse (get-property-paths (get-field-keys schema))))
