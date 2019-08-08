(ns cliente.core
  (:gen-class))
(require '[clojure.java.io :as io]
         '[clojure.data.codec.base64 :as b64]
         '[clojure.data.json :as json])
(import '[java.net DatagramSocket
          DatagramPacket
          InetSocketAddress
          InetAddress
          Socket]
        '[java.io StringWriter]
        '[java.util.concurrent TimeoutException TimeUnit])

(load "servidores")
(load "tcp")
(load "udp")
(load "timeout")

(defn main-loop
  [socket handler]
  (println "Digite o nome do arquivo buscado: ")
  (let [nome-arquivo (read-line)]
      (println "Time-to-live da busca: ")
      (let [ttl (Integer. (read-line))]
        (solicita-arquivo nome-arquivo socket ttl)
        (let [requisicao (com-timeout 10000 (recebe-requisicao-ret socket))]
          (if requisicao
            (trata-requisicao-ret requisicao)
            (System/exit 0))))))

(defn -main
  "Ponto de entrada de 'lein run'"
  [& args]
  (let [socket (DatagramSocket. 9442)
        handler trata-requisicao-ret]
    (loop-alvos!)
    (main-loop socket handler)))




