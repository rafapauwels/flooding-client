(in-ns 'cliente.core)

(def alvos [{:endereco-ip "localhost" :porta 9443}]) ; Usar peers da cloud

(defn escolhe-servidor-alvo
  "Escolhe um alvo da lista de alvos aleatoriamente"
  [alvos]
  (rand-nth alvos))
