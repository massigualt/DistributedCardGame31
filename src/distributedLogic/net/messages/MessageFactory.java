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
     * @param howManyCrash
     * @return
     */
    public GameMessage newGameMessage(Move move, int messageCounter, int howManyCrash) {
        return new GameMessage(myId, messageCounter, move, howManyCrash);
    }

    // TODO provvisorio
    public GameMessage newGameMessage(String move, int messageCounter, int howManyCrash) {
        return new GameMessage(myId, messageCounter, move, howManyCrash);
    }


    /**
     * Creazione di un GameMessage utilizzato per notificare i crash dei nodi
     *
     * @param nodeCrashedId
     * @param messageCounter
     * @param howManyCrash
     * @return
     */
    public GameMessage newCrashMessage(int nodeCrashedId, int messageCounter, int howManyCrash) {
        return new GameMessage(myId, messageCounter, nodeCrashedId, howManyCrash);
    }
}
