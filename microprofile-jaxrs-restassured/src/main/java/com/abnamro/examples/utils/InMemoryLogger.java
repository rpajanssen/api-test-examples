package com.abnamro.examples.utils;

import com.abnamro.examples.aspects.Logger;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class InMemoryLogger implements Logger {
    private static final String LOG_STATEMENT_TEMPLATE = "%s %s";

    private static final List<String> LOG_STATEMENTS = new ArrayList<>();

    @Override
    public void debug(final String loggingClassName, final String message) {
        getLogStatements().add(
                String.format(LOG_STATEMENT_TEMPLATE, loggingClassName, message)
        );
    }

    public static List<String> getLogStatements() {
        return LOG_STATEMENTS;
    }

    public static void reset() {
        getLogStatements().clear();
    }
}
