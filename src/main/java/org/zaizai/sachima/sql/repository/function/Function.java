package org.zaizai.sachima.sql.repository.function;

public interface Function {
    FunctionType getType();

    FunctionHandler findHandler();
    FunctionHandler findHandler(String signature);
}
