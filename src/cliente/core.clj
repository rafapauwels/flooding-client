(ns cliente.core)
(require '[clojure.java.io :as io])
(import '[java.net DatagramSocket
          DatagramPacket
          InetSocketAddress
          InetAddress
          Socket]
        '[java.io StringWriter])

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

(defn solicita-arquivo
  "TTL default 3, monta e envia requisição usando funções auxiliares"
  ([nome-arquivo]
   (solicita-arquivo nome-arquivo 3))
  ([nome-arquivo ttl]
   (let [servidor-alvo (escolhe-servidor-alvo alvos)
         query-pesquisa (constroi-query nome-arquivo ttl)]
     (println (str "Enviando pesquisa por " nome-arquivo " para " servidor-alvo))
     (enviar-query socket query-pesquisa servidor-alvo))))

(defn recebe-requisicao-ret
  [^DatagramSocket socket]
  (let [buffer (byte-array 512)
        pacote (DatagramPacket. buffer 512)]
    (println "Recebido retorno da solicitação")
    (.receive socket pacote)
    (String. (.getData pacote)
             0 (.getLength pacote))))

(defn loop-recebimento-requisicoes-ret 
  [socket f]
  (future (while true (f (recebe-requisicao-ret socket)))))

(defn solicita-transferencia
  [endereco-tcp porta caminho-arquivo]
  (with-open [sock (Socket. endereco-tcp porta)
              writer (io/writer sock)
              reader (io/reader sock)
              resposta (StringWriter.)]
    (.append writer (str "GET " caminho-arquivo "\n"))
    (.flush writer)
    (io/copy reader resposta)
    (str resposta)))

(defn trata-requisicao-ret
  [requisicao-ret]
  (println (str requisicao-ret))
  (let [ret-mapeado (clojure.edn/read-string requisicao-ret)
        endereco-tcp (:endereco-tcp ret-mapeado)
        caminho-arquivo (:caminho-arquivo ret-mapeado)]
    (solicita-transferencia endereco-tcp 9443 caminho-arquivo)))
