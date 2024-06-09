(ns blockchain.blockchain
  (:require
   [blockchain.hasher :as hasher]
   [financeiro.db :as db]
   [financeiro.transacoes :refer [valida?]]))

(def blockchain-financeira (atom []))
(def informacoes-bloco (atom []))

(defn registros-financeiros [] @blockchain-financeira)
(defn blocos-blockchain [] @informacoes-bloco)

(defn valido? [bloco]
  (if (nil? bloco) true (valida? bloco)))

(defn registrar [bloco]
  (let [colecao-atualizada (if (nil? bloco)
                             (swap! blockchain-financeira conj (db/transacoes))
                             (do (swap! db/registros conj bloco)
                                 (swap! blockchain-financeira conj (db/transacoes))))
        hash-genesis (format "%064d" 0)
        tamanho-atual (count colecao-atualizada)
        hash-anterior (cond (= tamanho-atual 1)
                            hash-genesis
                            :else (:atual (nth (blocos-blockchain) (- tamanho-atual 2))))
        nonce (hasher/minerar tamanho-atual
                              (db/transacoes)
                              hash-anterior)]
    (println "TO AQUI")
    (println (db/transacoes))
    (swap! informacoes-bloco conj
           (if (nil? bloco)
             (merge bloco {:id tamanho-atual
                           :dados (db/transacoes)
                           :nonce  nonce
                           :anterior hash-anterior
                           :atual  (hasher/hash_proprio tamanho-atual
                                                        nonce
                                                        (db/transacoes)
                                                        hash-anterior)})
             {:id tamanho-atual
              :dados (db/transacoes)
              :nonce  nonce
              :anterior hash-anterior
              :atual  (hasher/hash_proprio tamanho-atual
                                           nonce
                                           (db/transacoes)
                                           hash-anterior)}))))