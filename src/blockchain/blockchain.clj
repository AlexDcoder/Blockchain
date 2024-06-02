(ns blockchain.blockchain
  (:require [blockchain.hasher :as b-hash]))

(def blockchain-financeira (atom []))

(defn registros-blockchain [] @blockchain-financeira)


(defn registrar [transacao]
  (let [blockchain-atualizada (swap! blockchain-financeira conj transacao)
        tamanho-atual (count blockchain-atualizada)
        hash-genesis "0000000000000000000000000000000000000000000000000000000000000000"]
    
    #_{:clj-kondo/ignore [:unused-value]}
    (merge transacao {:id tamanho-atual})
    (if (= 1 tamanho-atual)
      (do
        #_{:clj-kondo/ignore [:unused-value]}
        (merge transacao {:anterior hash-genesis
                          :nonce (b-hash/achar-nounce (:id transacao) 
                                                      (str (:tipo transacao) (:valor transacao)) 
                                                      hash-genesis)})
        
        (merge transacao {:atual (b-hash/hash_proprio 
                                  (:id transacao) 
                                  (:nonce transacao) 
                                  (str (:valor transacao) (:tipo transacao)) 
                                  hash-genesis)}))
    
      (do 
        #_{:clj-kondo/ignore [:unused-value]}
        (merge transacao {:anterior (nth (registros-blockchain) (dec tamanho-atual)) 
                          :nonce (b-hash/achar-nounce (:id transacao) 
                                                      (str (:valor transacao) (:tipo transacao)) 
                                                      (:anterior (nth (registros-blockchain) (dec tamanho-atual))))})
        
        (merge transacao {:atual (b-hash/hash_proprio (:id transacao)
                                                      (:nonce transacao) 
                                                      (str (:valor transacao) (:tipo transacao)) 
                                                      (:anterior (nth (registros-blockchain) (dec tamanho-atual))))})))))