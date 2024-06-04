(ns blockchain.blockchain
  (:require
   [blockchain.hasher :as b-hash]))

(def blockchain-financeira (atom []))

(defn registros-blockchain [] @blockchain-financeira)

(defn registrar [transacoes]
  (let [colecao-atualizada (swap! blockchain-financeira conj transacoes)
        hash-genesis (format "%064d" 0)
        tamanho-atual (count colecao-atualizada)]
    (if (= tamanho-atual 1)
      (merge transacoes {:id tamanho-atual
                         :anterior hash-genesis
                         :nonce (b-hash/achar-nounce tamanho-atual colecao-atualizada hash-genesis)})
      (merge transacoes {:id tamanho-atual
                         :anterior (:anterior (nth colecao-atualizada (dec tamanho-atual)))
                         :nonce (b-hash/achar-nounce tamanho-atual colecao-atualizada hash-genesis)}))))