package com.minecave.errors.listener;/*
 * Copyright (C) 2011-Current Carter Gale (Ktar5) <buildfresh@gmail.com>
 * 
 * This file is part of errors.
 * 
 * errors can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */

import com.minecave.errors.ErrorHandling;
import com.minecave.errors.loggers.ErrorLogger;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class EnableErrorHandler extends Handler {
    @Override
    public void publish(LogRecord record) {
        try {
            handleLog(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLog(final LogRecord record) throws IOException {
        if (record.getThrown() != null) {
            ErrorLogger.log(record.getThrown(), "onEnable");
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }

    public static void register() throws IOException {
        ErrorHandling eh = ErrorHandling.getInstance();

        String filePath = eh.getDataFolder() + File.separator + "enableLog.log";

        File file = new File(filePath);
        if(file.exists()) file.delete();
        file.createNewFile();

        FileHandler handler = new FileHandler(filePath,  true);
            handler.setLevel(Level.WARNING);

        Bukkit.getServer().getLogger().addHandler(handler);
        //handler.setFormatter(new SimpleFormatter());
    }
}
