(ns blockchain.blockchain
  (:require
   [blockchain.hasher :as b-hash]
   [financeiro.db :as db]))

(def blockchain-financeira (atom []))

(defn registros-blockchain [] @blockchain-financeira)

(defn validar-bloco [] ())

(defn registrar [bloco]
  (let [colecao-atualizada (swap! blockchain-financeira conj bloco)
        hash-genesis (format "%064d" 0)
        tamanho-atual (count colecao-atualizada)]
    (println "chegou até aqui")))