(ns cliente.core)
(require '[clojure.java.io :as io])
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
        ;(com-timeout 10000 (recebe-requisicao-ret socket))
        (let [requisicao ((recebe-requisicao-ret socket))]
          (println (str "req: " requisicao))
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
