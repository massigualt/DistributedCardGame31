package distributedLogic.net.messages;

import distributedLogic.game.Move;

/**
 * Classe MessageFactory, si comporta come la classe RouterFactory ma con i messaggi.
 */
public class MessageFactory {
    private int myId;

    public MessageFactory(int myId) {
        this.myId = myId;
    }

    /**
     * Creazione di un GameMessage classico dove è contenuta la mossa effettuata
     *
     * @param move
     * @param messageCounter
     * @return
     */
    public GameMessage newGameMessage(Move move, int messageCounter) {
        return new GameMessage(myId, messageCounter, move);
    }

    /**
     * Creazione di un GameMessage utilizzato per notificare i crash dei nodi
     *
     * @param nodeCrashedId
     * @param messageCounter
     * @return
     */
    public GameMessage newCrashMessage(int nodeCrashedId, int messageCounter) {
        return new GameMessage(myId, messageCounter, nodeCrashedId);
    }
}
