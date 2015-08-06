package com.minecave.errors.listener;

import com.minecave.errors.ErrorHandling;
import com.minecave.errors.loggers.ErrorLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

/*
 * Copyright (C) 2011-Current Carter Gale (Ktar5) <buildfresh@gmail.com>
 *
 * This file is part of errors.
 *
 * errors can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */

public class ExceptionListener extends PluginLogger {

    private String pluginN;

    public ExceptionListener(Plugin context) {
        super(context);

        try {
            Field field = PluginLogger.class.getDeclaredField("pluginName");
            field.setAccessible(true);

            pluginN = (String) field.get(this);
            pluginN = pluginN
                    .substring(0, pluginN.length()-1)
                    .replaceAll(Pattern.quote("["), "")
                    .replaceAll(Pattern.quote("]"), "");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ErrorHandling.getInstance().getThreshold().addPlugin(pluginN);
    }

    @Override
    public void log(LogRecord logRecord) {
        try {
            handleLog(logRecord);
        } catch (IOException e) {e.printStackTrace();}
        super.log(logRecord);
    }

    private void handleLog(final LogRecord record) throws IOException {
        if (record.getThrown() != null) {
            ErrorLogger.log(record.getThrown(), pluginN);
        }
    }

    public static void register() throws Exception {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            JavaPlugin javaPlugin = (JavaPlugin) plugin;
            ExceptionListener logger = new ExceptionListener(javaPlugin);
            Field field = JavaPlugin.class.getDeclaredField("logger");

            field.setAccessible(true);
            field.set(javaPlugin, logger);
        }
    }
}

