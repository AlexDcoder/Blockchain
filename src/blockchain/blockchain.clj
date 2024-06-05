(ns blockchain.blockchain
  (:require
   [blockchain.hasher :as b-hash]
   [financeiro.db :as db]
   [financeiro.transacoes :refer [valida?]]
   ))

(def blockchain-financeira (atom []))

(defn registros-blockchain [] @blockchain-financeira)

(defn bloco-valido? [bloco] (if (valida? bloco) 
                              () 
                              ()))

(defn registrar [bloco]
  (let [colecao-atualizada (swap! blockchain-financeira conj bloco)
        hash-genesis (format "%064d" 0)
        tamanho-atual (count colecao-atualizada)]
    (merge bloco {:id tamanho-atual 
                  :anterior (cond (= tamanho-atual 1) 
                                  hash-genesis 
                                  :else (:anterior (nth colecao-atualizada (- tamanho-atual 2))))
                  :nonce (cond (= tamanho-atual 1) () :else ())
                  :atual ()})))