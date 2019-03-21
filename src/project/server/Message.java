package project.server;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private String username;
    private String msg;
    private String type;
    private Date date;

    public Message(String username, String msg, String type, Date date) {
        this.username = username;
        this.msg = msg;
        this.type = type;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
