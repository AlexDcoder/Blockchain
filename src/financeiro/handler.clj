(ns financeiro.handler
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [blockchain.blockchain :as blockchain]
            [financeiro.db :as db]
            [financeiro.transacoes :as transacoes]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [selmer.parser :as parser]))

(parser/set-resource-path! (io/resource "public"))

(defn renderizar-template [nome-arquivo nome-pagina]
  (let [cominho-template (format "%s.html" nome-arquivo)]
    (parser/render-file cominho-template {:title nome-pagina})))

(defn como-json [conteudo & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body (json/generate-string conteudo)})

(defroutes app-routes
  ;; Operações da parte financeira
  (GET "/" ;; Página inicial
    []
    (renderizar-template "index" "Índice"))

  (GET "/saldo" ;; Mostrar saldo atual
    []
    (como-json {:saldo (db/saldo)}))

  (POST "/transacoes" ;; Realizar Transação
    requisicao
    (println (:params requisicao))
    (let [transferencia {:valor (Integer/parseInt (:valor (:params requisicao)))
                         :tipo (:tipo (:params requisicao))}]
      (if (transacoes/valida? transferencia)
        (-> (db/cadastrar transferencia)
            (como-json 201))
        (como-json {:mensagem "Requisição inválida"} 422))))

  (GET "/transacoes" ;; Mostrar todas as transações com filtro ou não
    {filtros :params}
    (como-json {:transacoes (if (empty? filtros) (db/transacoes) (db/transacoes-com-filtro filtros))}))

  (GET "/receitas" ;;Mostrar transações que são somente do tipo RECEITA
    []
    (como-json {:transacoes (db/transacoes-do-tipo "receita")}))

  (GET "/despesas" ;;Mostrar transações que são somente do tipo DESPESA
    []
    (como-json {:transacoes (db/transacoes-do-tipo "despesa")}))

  ;; Operações da parte blockchain
  (GET "/blockchain" ;; Mostrar a blockchain de transações
    []
    (como-json {:blockchain (blockchain/blocos-blockchain)}))

  (POST "/blockchain" ;; Fazer backup das transações até o momento
    requisicao
    (let [bloco (if (and (= "" (:valor (:params requisicao)))
                         (= "" (:tipo (:params requisicao))))
                  nil
                  {:valor (Integer/parseInt (:valor (:params requisicao)))
                   :tipo (:tipo (:params requisicao))})]
      (if (blockchain/valido? bloco)
        (-> (blockchain/registrar bloco)
            (como-json 201))
        (como-json {:mensagem "Requisição inválida"} 422)))))

(def app
  (-> app-routes
      (wrap-json-body {:keywords? true :bigdecimals? true})
      (wrap-defaults api-defaults)))

