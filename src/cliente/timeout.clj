(in-ns 'cliente.core)

(defmacro com-timeout [millis & body]
  `(let [future# (future ~@body)]
     (try
       (.get future# ~millis java.util.concurrent.TimeUnit/MILLISECONDS)
       (catch java.util.concurrent.TimeoutException x# 
         (do
           (future-cancel future#)
           (println "Tempo esgotado"))))))
