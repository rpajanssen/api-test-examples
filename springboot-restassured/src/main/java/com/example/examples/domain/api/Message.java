package com.example.examples.domain.api;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    public Message() {
        // required by (de)serialization framework
    }

    public Message(
            @NotEmpty(message = "title is not allowed to be empty") String title,
            @NotEmpty(message = "body is not allowed to be empty") String body
    ) {
        this.title = title;
        this.body = body;
    }

    @NotEmpty(message = "title is not allowed to be empty")
    private String title;

    @NotEmpty(message = "body is not allowed to be empty")
    private String body;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Message{" + "title='" + title + "', body='" + body + "'}";
    }
}
