# DistributedCardGame31

Distributed31 è la rappresentazione del gioco di carte 31. Il sistema ha un'architettura distribuita e resiliente in modo da resistere ad eventuali crash dei peer presenti all'interno della rete. 
Il sistema è stato sviluppato utilizzando JavaRMI. 

Una volta generati i file jar è possibile avviare il sistema nel seguente modo:
### Start Server
```console
java -jar Server.jar 20
```
Il server in questione viene utilizzato per permettere a tutti i giocatori di entrare nella lobby, una volta scaduta la variante tempo (in questo caso 20 secondi) il server viene chiuso e l'architettura del sistema diventa completamente distribuita

### Start Client
```console
java -jar Client.jar
```
