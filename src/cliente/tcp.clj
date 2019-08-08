(in-ns 'cliente.core)

(defn converte-arquivo
  [caminho-do-arquivo]
  (with-open [in (io/input-stream (str caminho-do-arquivo "~"))
            out (io/output-stream caminho-do-arquivo)]
  (b64/decoding-transfer in out))
  (io/delete-file (str caminho-do-arquivo "~"))
  (System/exit 0))

(defn base64->arquivo
  [b64 destino]
  (println (str "Arquivo recebido, salvando em " destino))
  (spit (str destino "~") (.toString b64))
  (converte-arquivo destino))

(defn solicita-transferencia
  [endereco-tcp porta caminho-arquivo nome-arquivo]
  (println (str "Solicitação de transferencia sendo enviada para " endereco-tcp))
  (with-open [sock (Socket. endereco-tcp porta)
              writer (io/writer sock)
              reader (io/reader sock)
              resposta (StringWriter.)]
    (.append writer (str caminho-arquivo "\n"))
    (.flush writer)
    (io/copy reader resposta)
    (base64->arquivo resposta (str "/home/ubuntu/" nome-arquivo))))
