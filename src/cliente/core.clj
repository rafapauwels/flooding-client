(ns cliente.core
  (:gen-class))
(require '[clojure.java.io :as io]
         '[clojure.data.codec.base64 :as b64])
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
  (println "\nDigite o nome do arquivo buscado: ")
  (let [nome-arquivo (read-line)]
      (println "Time-to-live da busca: ")
      (let [ttl (Integer. (read-line))]
        (solicita-arquivo nome-arquivo socket ttl)
;(com-timeout 1000 f)
        (let [requisicao (recebe-requisicao-ret socket)]
          (if requisicao
            (trata-requisicao-ret requisicao)
            (main-loop socket handler))))))

(defn -main
  "Ponto de entrada de 'lein run'"
  [& args]
  (let [socket (DatagramSocket. 9442)
        handler trata-requisicao-ret]
    (main-loop socket handler)))



;https://stackoverflow.com/questions/6694530/executing-a-function-with-a-timeout
