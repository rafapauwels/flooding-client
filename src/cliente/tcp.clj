(in-ns 'cliente.core)

(defn base64->arquivo
  [b64 destino]
  (println (str "b64: " b64 ", dest: " destino)))

(defn solicita-transferencia
  [endereco-tcp porta caminho-arquivo]
  (println (str "Solicitação de transferencia sendo enviada para " endereco-tcp))
  (with-open [sock (Socket. endereco-tcp porta)
              writer (io/writer sock)
              reader (io/reader sock)
              resposta (StringWriter.)]
    (.append writer (str "GET " caminho-arquivo "\n"))
    (.flush writer)
    (io/copy reader resposta)
    (str resposta)
    (base64->arquivo resposta "/home/pauwels/Desktop/omg")))
