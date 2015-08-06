package com.minecave.errors;

import com.minecave.errors.commands.Commands;
import com.minecave.errors.listener.ExceptionListener;
import lombok.Getter;
import me.ktar5.slackapi.SlackApi;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

/*
 * Copyright (C) 2011-Current Carter Gale (Ktar5) <buildfresh@gmail.com>
 *
 * This file is part of errors.
 *
 * errors can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */

public class ErrorHandling extends JavaPlugin{

    @Getter
    private ThresholdHandler threshold;
    @Getter
    private static ErrorHandling instance = null;
    @Getter
    private SlackApi api = null;

    @Override
    public void onLoad(){
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        instance = this;
        api = new SlackApi(this.getConfig().getString("slack.token"));
        threshold = new ThresholdHandler(
                this.getConfig().getInt("log.threshold.time-to-check"),
                this.getConfig().getInt("log.threshold.logs-allowed"));
        try {
            ExceptionListener.register();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerEnableHandler(){

    }

    @Override
    public void onEnable(){
        threshold.start();
        this.getCommand("error").setExecutor(new Commands());
        if(this.getConfig().getBoolean("debug")){
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                throw new RuntimeException("So, it works eh?");
            }, 150L);
        }
    }

    public void load(){

    }

    @Override
    public void onDisable(){
        threshold.stop();
        instance = null;
    }

}
