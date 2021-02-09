package icefozen.java.kaantip;

public class ChatModel {

    private String message;

    private String sender;

    private String room;

    public ChatModel(String message, String room, String sender){

        this.message = message;
        this.sender = sender;
        this.room = room;
    }
    public ChatModel(String message, String room){

        this.message = message;
        this.room = room;
    }

    public ChatModel(String message) {
        this.message = message;
    }

    public ChatModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
