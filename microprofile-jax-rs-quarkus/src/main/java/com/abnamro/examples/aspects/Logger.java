package com.abnamro.examples.aspects;

import java.io.Serializable;

public interface Logger extends Serializable {
    void debug(String loggingClassName, String message);
}
