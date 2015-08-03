package com.minecave.errors;

import com.minecave.errors.commands.Commands;
import com.minecave.errors.listener.ExceptionListener;
import lombok.Getter;
import me.ktar5.slackapi.SlackApi;
import me.ktar5.slackapi.SlackAttachment;
import me.ktar5.slackapi.SlackField;
import me.ktar5.slackapi.SlackMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by Carter on 7/29/2015.
 */
public class ErrorHandling extends JavaPlugin{

    @Getter
    private ThresholdHandler threshold;
    @Getter
    private static ErrorHandling instance = null;
    private SlackApi api = null;

    @Override
    public void onEnable(){
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        api = new SlackApi(this.getConfig().getString("slack.token"));
        instance = this;
        threshold = new ThresholdHandler(
                this.getConfig().getInt("log.threshold.time-to-check"),
                this.getConfig().getInt("log.threshold.logs-allowed"));
        try {
            ExceptionListener.register();
        } catch (Exception e) {
            e.printStackTrace();
        }
        threshold.start();
        this.getCommand("error").setExecutor(new Commands());

        if(this.getConfig().getBoolean("debug")){
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                throw new RuntimeException("So, it works eh?");
            }, 150L);
        }
    }

    @Override
    public void onDisable(){
        threshold.stop();
        instance = null;
    }

    public void send(String message, String time, String pluginName){
        //TO CREATE THE FIRST LINER SUMMARY
        String[] firstLine = message.split("\n", 3);
        String summary = "A " + firstLine[0] + firstLine[1];
        summary = summary.replaceAll(Pattern.quote("\t")," ");

        //TO CREATE THE CHANNEL NAME
        String channel = this.getConfig().getBoolean("slack.pm.enabled") ?
                "@" + this.getConfig().getString("slack.pm.user").toLowerCase()
                : null;

        //PUTTING IT ALL TOGETHER
        SlackMessage msg = new SlackMessage(summary)
                .addAttachments(
                        new SlackAttachment()
                                .setFallback(summary)
                                .setText("```" + message.replaceAll(Pattern.quote("\n"),"") + "```")
                                .setFields(new ArrayList<>(Arrays.asList(
                                        new SlackField(true, "Server", "_" + this.getConfig().getString("slack.server-name") + "_"),
                                        new SlackField(true, "Version", "_" + this.getServer().getBukkitVersion() + "_"),
                                        new SlackField(true, "Date", "_" + time + "_"),
                                        new SlackField(true, "Plugin", "_" + pluginName + "_"))))
                                .setColor("danger")
                                .addAllowedMarkdown("text")
                                .addAllowedMarkdown("fields")
                                .addTitle("Stack Trace"))
                .setChannel(channel);
        api.call(msg);
    }

}
