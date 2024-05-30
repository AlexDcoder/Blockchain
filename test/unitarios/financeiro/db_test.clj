(ns financeiro.db-test
  (:require [midje.sweet :refer :all]
            [financeiro.db :refer :all]))

(facts "Guarda uma transação num átomo"
       (against-background [(before :facts (limpar))]
                           (fact "a coleção de transações inicia vazia"
                                 (count (transacoes)) => 1)
                           (fact "a transação é o primeiro registro"
                                 (cadastrar {:valor 7 :tipo "receita"})  => {:id 1 :valor 7 :tipo "receita"}
                                 (count (transacoes)) => 1)))

(facts "Calcula o saldo dada uma coleção de transações"
       (against-background [(before :facts (limpar))]

                           (fact "saldo é positivo quando só tem receita"
                                 (cadastrar {:valor 1 :tipo "receita"})
                                 (cadastrar {:valor 10 :tipo "receita"})
                                 (cadastrar {:valor 100 :tipo "receita"})
                                 (cadastrar {:valor 1000 :tipo "receita"})
                                 (saldo) => 1111)

                           (fact "saldo é negativo quando só tem despesa"
                                 (cadastrar {:valor 2 :tipo "despesa"})
                                 (cadastrar {:valor 20 :tipo "despesa"})
                                 (cadastrar {:valor 200 :tipo "despesa"})
                                 (cadastrar {:valor 2000 :tipo "despesa"})
                                 (saldo) => -222)

                           (fact "saldo é a soma das receitas menos a soma das despesas"
                                 (cadastrar {:valor 2 :tipo "despesa"})
                                 (cadastrar {:valor 10 :tipo "receita"})
                                 (cadastrar {:valor 200 :tipo "despesa"})
                                 (cadastrar {:valor 1000 :tipo "receita"})
                                 (saldo) => 808)))

(facts "filtra transações por tipo"
       (def transacoes-aleatorias '({:valor 2 :tipo "despesa"} {:valor 10 :tipo "receita"} {:valor 200 :tipo "despesa"} {:valor 1000 :tipo "receita"}))

       (against-background [(before :facts [(limpar) (doseq [transacao transacoes-aleatorias] (cadastrar transacao))])]
                           (fact "encontra apenas as receitas" (transacoes-do-tipo "receita") => '({:valor 10 :tipo "receita"} {:valor 1000 :tipo "receita"}))
                           (fact "encontra apenas as despesas" (transacoes-do-tipo "despesa") => '({:valor 10 :tipo "despesa"} {:valor 1000 :tipo "despesa"}))))

(facts "filtra	transações	por	rótulo"
       (def	transacoes-aleatorias
         '({:valor	7.0M	:tipo	"despesa"
            :rotulos	["sorvete" "entretenimento"]}
           {:valor	88.0M	:tipo	"despesa"
            :rotulos	["livro" "educação"]}
           {:valor	106.0M	:tipo	"despesa"
            :rotulos	["curso" "educação"]}
           {:valor	8000.0M	:tipo	"receita"
            :rotulos	["salário"]}))
       (against-background
        [(before	:facts	[(limpar) (doseq	[transacao	transacoes-aleatorias]
                                    (cadastrar	transacao))])]
        (fact "encontra	a	transação	com	rótulo	'salário'"
              (transacoes-com-filtro	{:rotulos	"salário"})
              =>	'({:valor	8000.0M	:tipo	"receita"
                    :rotulos	["salário"]}))
        (fact "encontra	as	2	transações	com	rótulo	'educação'"
              (transacoes-com-filtro	{:rotulos	["educação"]})
              =>	'({:valor	88.0M	:tipo	"despesa"
                    :rotulos	["livro" "educação"]}
                   {:valor	106.0M	:tipo	"despesa"
                    :rotulos	["curso" "educação"]}))
        (fact "encontra	as	2	transações	com	rótulo	'livro'	ou	'curso'"
              (transacoes-com-filtro	{:rotulos	["livro" "curso"]})
              =>	'({:valor	88.0M	:tipo	"despesa"
                    :rotulos	["livro" "educação"]}
                   {:valor	106.0M	:tipo	"despesa"
                    :rotulos	["curso" "educação"]}))))