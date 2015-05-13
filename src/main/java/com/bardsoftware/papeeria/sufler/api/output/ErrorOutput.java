package com.bardsoftware.papeeria.sufler.api.output;

public class ErrorOutput {
    private String myMessage;

    public String getError() {
        return myMessage;
    }

    public void setError(String myMessage) {
        this.myMessage = myMessage;
    }

    public ErrorOutput(String message) {
        myMessage = message;

    }
}
