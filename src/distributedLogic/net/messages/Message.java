package distributedLogic.net.messages;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private String playerName;
    private String message;
    private Date date;

    public Message(String playerName, String message, Date date) {
        this.playerName = playerName;
        this.message = message;
        this.date = date;
    }

    @Override
    public String toString() {
        return "[ " + this.playerName + " ] [ " + this.date + " ] -> " + this.message;
    }
}
