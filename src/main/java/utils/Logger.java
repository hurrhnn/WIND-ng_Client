package utils;

import org.slf4j.LoggerFactory;

public class Logger {
    public static org.slf4j.Logger getLogger(Class<?> targetClass) {
        return LoggerFactory.getLogger(targetClass);
    }
}
