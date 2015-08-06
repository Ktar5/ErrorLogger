package com.minecave.errors.loggers;

import com.minecave.errors.ErrorHandling;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Copyright (C) 2011-Current Carter Gale (Ktar5) <buildfresh@gmail.com>
 *
 * This file is part of errors.
 *
 * errors can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */

public class ErrorLogger {

    public static void log(Throwable thrown, String pluginName) throws IOException {
        //Get an instance of the config from the main plugin class, keep it here for performance improvements
        FileConfiguration config = ErrorHandling.getInstance().getConfig();

        //Threshold checks
        if(ErrorHandling.getInstance().getThreshold().isHalted(pluginName) &&
                config.getBoolean("log.threshold.enabled")){
            //If thresholding is enabled and the plugin is halted, ignore this log call and return
            return;
        }

        // Calculate the date & time of this log and store it in a string, for use later
        String dateAndTime = new SimpleDateFormat("HH:mm:ss @ MM-dd-yyyy").format(new Date());

        //If logging to a file is enabled, log to the file
        if(config.getBoolean("log.enabled")) {
            FileLogger.logToFile(thrown, dateAndTime, pluginName, config.getBoolean("log.shortener.enabled"));
        }

        //If logging to slack is enabled, log to slack
        if(config.getBoolean("slack.enabled")){
           SlackLogger.logToSlack(ExceptionUtils.getStackTrace(thrown), config, dateAndTime, pluginName);
        }

    }

}
