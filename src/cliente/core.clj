(ns cliente.core)
(import '[java.net DatagramSocket
          DatagramPacket
          InetSocketAddress
          InetAddress])

(comment  (def socket (DatagramSocket. 9442)))
(def alvos [{:endereco-ip "localhost" :porta 9443}]) ; Usar peers da cloud

(defn constroi-query
  "Monta mapa que será enviado aos servidores"
  [query time-to-live]
  (zipmap [:endereco-origem :query :time-to-live]
          [(.getHostAddress (InetAddress/getLocalHost))
           query
           time-to-live]))

(defn escolhe-servidor-alvo
  "Escolhe um alvo da lista de alvos aleatoriamente"
  [alvos]
  (rand-nth alvos))

(defn enviar-query
  "Envia uma requisição através de um socket para o alvo, definido pelo endereço e porta. Caso a mensagem ultrapasse 512 bytes
  ela será truncada"
  [^DatagramSocket socket query alvo]
  (let [payload (.getBytes (str query))
        tamanho-requisicao (min (alength payload) 512)
        endereco (InetSocketAddress. (:endereco-ip alvo) (:porta alvo))
        pacote (DatagramPacket. payload tamanho-requisicao endereco)]
    (.send socket pacote)))

(defn solicitar-arquivo
  "TTL default 3, monta e envia requisição usando funções auxiliares"
  ([nome-arquivo]
   (solicitar-arquivo nome-arquivo 3))
  ([nome-arquivo ttl]
   (let [servidor-alvo (escolhe-servidor-alvo alvos)
         query-pesquisa (constroi-query nome-arquivo ttl)]
     (println (str "Enviando pesquisa por " nome-arquivo " para " servidor-alvo))
     (enviar-query socket query-pesquisa servidor-alvo))))
