(ns financeiro.transacoes-test
  (require [midje.sweet :refer :all]
           [financeiro.transacoes :refer :all]))

(fact "Uma transação sem valor não é valida"
      (valida? {:tipo "receita"}) => false)

(fact "Uma transação com valor negativo não é válida"
      (valida? {:valor -10 :tipo "receita"}) => false)

(fact "Uma transação com valor não numérico não é válida"
      (valida? {:valor "mil" :tipo "receita"}) => false)

(fact "Uma transação com sem tipo não é válida"
      (valida? {:valor 90}) => false)

(fact "Uma transação com tipo desconhecido não é válida"
      (valida? {:valor 8 :tipo "investimento"}) => false)

(fact "Uma transação com valor numérico positivp e com tipo conhecido é válida"
      (valida? {:valor 230 :tipo "receita"}) => true)


