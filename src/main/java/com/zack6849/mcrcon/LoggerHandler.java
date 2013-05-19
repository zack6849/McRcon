package com.zack6849.mcrcon;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LoggerHandler extends Handler {

    public BufferedWriter out;

    public LoggerHandler(BufferedWriter out) {
        this.out = out;
    }

    @Override
    public void close() throws SecurityException {
        return;
    }

    @Override
    public void flush() {
        return;
    }

    @Override
    public void publish(LogRecord e) {
        try {
            String time = String.format("%tF %<tT ", new Date());
            out.write(time + "[" + e.getLevel().getName() + "] " + e.getMessage());
            out.newLine();
            out.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
}
