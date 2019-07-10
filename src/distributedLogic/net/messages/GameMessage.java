package distributedLogic.net.messages;

import distributedLogic.game.Move;


public class GameMessage extends Message implements Cloneable {

    private Move move;
    private int nodeCrashedId;

    /**
     * Metodo che inizializza un GameMessage classico
     *
     * @param originId
     * @param id
     */
    public GameMessage(int originId, int id, Move move) {
        super(originId, id);
        this.move = move;
        this.nodeCrashedId = -1;
    }

    /**
     * Metodo che inizializza un GameMessage utilizzato per informare
     * la rete del crash di un dato nodo.
     *
     * @param origId
     * @param id
     * @param nodeCrashedId
     */
    public GameMessage(int origId, int id, int nodeCrashedId) {
        super(origId, id);
        this.nodeCrashedId = nodeCrashedId;
        this.move = null;
    }


    public String toString() {
        String string;
        if (this.getNodeCrashed() != -1) {
            string = "Node crashed: " + this.nodeCrashedId;
        } else {
            string = "Game msg: " + this.move.toString();
        }

        return "\u001B[95m " + super.toString() + " { " + string + " } \u001B[0m";
    }

    /**
     * Metodo utilizzato per clonare un istanza della classe
     *
     * @return
     */
    public Object clone() {
        GameMessage m;
        if (nodeCrashedId == -1) {
            m = new GameMessage(getOriginId(), getMessageId(), move);
        } else {
            m = new GameMessage(getOriginId(), getMessageId(), nodeCrashedId);
        }
        m.setFromId(getFromId()); // aggiorno il nodo che manda il mex
        return m;
    }

    public Move getMove() {
        return move;
    }

    public int getNodeCrashed() {
        return nodeCrashedId;
    }
}
