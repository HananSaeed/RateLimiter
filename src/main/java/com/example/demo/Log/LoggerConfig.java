package com.example.demo.Log;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerConfig {
    private static final Logger logger = Logger.getLogger("AbnormalBehaviorLogger");

    static {
        try {
            FileHandler fileHandler = new FileHandler("abnormal_behavior.log", true); // Append mode
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false); // Prevent logging to console
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger getLogger() {
        return logger;
    }
}
