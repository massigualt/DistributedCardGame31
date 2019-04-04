package distributedLogic.net.messages;

import distributedLogic.game.Move;

import java.util.Date;

public class GameMessage extends Message implements Cloneable {

    private int id;
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
        this.id = id;
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
        this.id = id;
        this.nodeCrashedId = nodeCrashedId;
        this.move = null;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return "# " + id + " -> " + super.toString();
    }

    /**
     * Metodo utilizzato per clonare un istanza della classe
     *
     * @return
     */
    public Object clone() {
        GameMessage m;
        if (nodeCrashedId == -1) {
            m = new GameMessage(getOriginId(), id, move);
        } else {
            m = new GameMessage(getOriginId(), id, nodeCrashedId);
        }
        m.setFromId(getFromId());
        return m;
    }

    public Move getMove() {
        return move;
    }

    public boolean getBusso() {
        return move.isBusso();
    }

    public int getNodeCrashed() {
        return nodeCrashedId;
    }
}
