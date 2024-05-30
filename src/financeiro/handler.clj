(ns financeiro.handler
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [compojure.core :refer :all]
            [compojure.route :as route]
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
  (GET "/" [] (renderizar-template "index" "Índice"))
  (GET "/saldo" [] (como-json {:saldo (db/saldo)}))
  (POST "/transacoes" requisicao
    (if (transacoes/valida? (:body requisicao))
      (-> (db/cadastrar (:body requisicao))
          (como-json 201))

      (como-json {:mensagem "Requisição inválida"} 422)))
  (GET "/transacoes" {filtros :params} (como-json {:transacoes (if (empty? filtros) (db/transacoes) (db/transacoes-com-filtro filtros))}))
  (GET "/receitas" [] como-json {:transacoes (db/transacoes-do-tipo "receita")})
  (GET "/despesas" [] como-json {:transacoes (db/transacoes-do-tipo "despesa")})
  (route/not-found "Recurso não encontrado"))

(def app
  (-> (wrap-defaults app-routes api-defaults) (wrap-json-body {:keywords? true :bigdecimals? true})))
