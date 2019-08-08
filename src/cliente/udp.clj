(in-ns 'cliente.core)

(defn constroi-query
  "Monta mapa que será enviado aos servidores"
  [query time-to-live]
  (zipmap [:endereco-origem :query :time-to-live]
          [(.getHostAddress (InetAddress/getLocalHost))
           query
           time-to-live]))

(defn enviar-query
  "Envia uma requisição através de um socket para o alvo, definido pelo endereço e porta. Caso a mensagem ultrapasse 512 bytes
  ela será truncada"
  [^DatagramSocket socket query alvo]
  (let [payload (.getBytes (str query))
        tamanho-requisicao (min (alength payload) 512)
        endereco (InetSocketAddress. (:endereco-ip alvo) (:porta alvo))
        pacote (DatagramPacket. payload tamanho-requisicao endereco)]
    (.send socket pacote)))

(defn recebe-requisicao-ret
  [^DatagramSocket socket]
  (let [buffer (byte-array 512)
        pacote (DatagramPacket. buffer 512)]
    (.receive socket pacote)
    (println "Recebido retorno da solicitação")
    (String. (.getData pacote)
             0 (.getLength pacote))))

(defn trata-requisicao-ret
  [requisicao-ret]
  (println (str requisicao-ret))
  (let [ret-mapeado (clojure.edn/read-string requisicao-ret)
        endereco-tcp (:endereco-tcp ret-mapeado)
        caminho-arquivo (:caminho-arquivo ret-mapeado)]
    (solicita-transferencia endereco-tcp 
                            9443 
                            caminho-arquivo
                            (last (clojure.string/split caminho-arquivo #"/")))))

(defn solicita-arquivo
  "TTL default 3, monta e envia requisição usando funções auxiliares"
  ([nome-arquivo socket]
   (solicita-arquivo nome-arquivo socket 3))
  ([nome-arquivo socket ttl]
   (let [servidor-alvo (escolhe-servidor-alvo alvos)
         query-pesquisa (constroi-query nome-arquivo ttl)]
     (println (str "Enviando pesquisa por " nome-arquivo " para " servidor-alvo))
     (enviar-query socket query-pesquisa servidor-alvo))))
