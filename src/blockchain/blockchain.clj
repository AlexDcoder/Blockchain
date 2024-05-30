(ns blockchain.blockchain
  (:require [blockchain.hasher :as b-hash]))

(def blockchain (atom []))

(defn limpar []
  (reset! blockchain []))

(defn registrar [transacao]
  (let [blockchain-atualizada (swap! blockchain conj transacao)]
    (merge transacao {:id (count blockchain-atualizada)})
    )
  )