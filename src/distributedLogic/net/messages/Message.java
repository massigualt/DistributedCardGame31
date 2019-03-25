package distributedLogic.net.messages;

import java.io.Serializable;


public class Message implements Serializable, Cloneable {

    private int originId;
    private int fromId;
    private int messageId;

    /**
     * Metodo utilizzato per creare un GameMessage
     *
     * @param originId
     * @param messageId
     */
    public Message(int originId, int messageId) {
        this.originId = originId;
        this.fromId = originId;
        this.messageId = messageId;
    }

    public int getOriginId() {
        return originId;
    }

    public int getFromId() {
        return fromId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public Object clone() {
        Message m = new Message(originId, messageId);
        m.setFromId(fromId);
        return m;
    }

    @Override
    public String toString() {
        return "Received from: " + fromId + ", created by: " + originId + ", mex id: " + messageId;
    }
}
