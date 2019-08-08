(in-ns 'cliente.core)

(def lista-alvos (atom '[{:endereco-ip "localhost" :porta 9443}]))

(defn escolhe-servidor-alvo
  "Escolhe um alvo da lista de alvos aleatoriamente"
  []
  (rand-nth @lista-alvos))

(defn obtem-dados-alvos!
  [caminho]
  (let [lista-ips (map :endereco (json/read-str (slurp caminho) :key-fn keyword))]
    (map #(zipmap [:endereco-ip :porta]
                  [% 9443]) lista-ips)))

(defn atualiza-alvos!
  []
  (reset! lista-alvos (into-array 
                      (obtem-dados-alvos! 
                       "https://arcane-stream-28575.herokuapp.com/peers"))))

(defn loop-alvos!
  []
  (let [running (atom true)]
    (future
      (while @running
        (do
          (println "Atualizando servidores conhecidos")
          (atualiza-alvos!)
          (Thread/sleep 20000)
          )))))
