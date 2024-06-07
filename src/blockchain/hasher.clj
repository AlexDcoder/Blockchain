(ns blockchain.hasher
  (:import java.security.MessageDigest))

(defn sha256 [string]
  (let [digest (.digest (MessageDigest/getInstance "SHA-256") (.getBytes string "UTF-8"))]
    (apply str (map (partial format "%02x") digest))))

(defn minerar
  ([id dados anterior] (minerar id 0 dados anterior))
  ([id nonce dados anterior]
   (if
    (= (subs (sha256 (str id nonce dados anterior)) 0 4) "0000")
     nonce
     (recur id (inc nonce) dados anterior))))

(defn hash_proprio [id nonce dados anterior]
  (sha256 (str id nonce dados anterior)))
