package com.example.examples.domain.api;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

public class IncompatibleMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    public IncompatibleMessage() {
        // required by (de)serialization framework
    }

    public IncompatibleMessage(
            @NotEmpty(message = "title is not allowed to be empty") String title,
            @NotEmpty(message = "text is not allowed to be empty") String text
    ) {
        this.title = title;
        this.text = text;
    }

    @NotEmpty(message = "title is not allowed to be empty")
    private String title;

    @NotEmpty(message = "text is not allowed to be empty")
    private String text;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "IncompatibleMessage{" + "title='" + title + "', text='" + text + "'}";
    }
}
