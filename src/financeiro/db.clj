(ns financeiro.db)
(def registros (atom []))

(defn transacoes [] @registros)

(defn limpar []
  (reset! registros []))

(defn despesa? [transacao]
  (= (:tipo transacao) "despesa"))

(defn- calcular [acumulado transacao]
  (let [valor (:valor transacao)]
    (if (despesa? transacao)
      (- acumulado valor)
      (+ acumulado valor))))

(defn saldo []
  (reduce calcular 0 @registros))

(defn registrar [transacao]
  (let [colecao-atualizada (swap! registros conj transacao)]
    (merge transacao {:id (count colecao-atualizada)})))

(defn transacoes-do-tipo [tipo] (filter #(= tipo (:tipo %)) (transacoes)))

(defn transacoes-com-filtro [filtros]
  (let [rotulos (->> (:rotulos filtros) (conj []) (flatten) (set))]
    (filter #(some rotulos (:rotulos %)) (transacoes))))