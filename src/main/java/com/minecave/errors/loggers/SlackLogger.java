package com.minecave.errors.loggers;

import com.minecave.errors.ErrorHandling;
import me.ktar5.slackapi.SlackAttachment;
import me.ktar5.slackapi.SlackField;
import me.ktar5.slackapi.SlackMessage;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by Carter on 8/3/2015.
 */
public class SlackLogger {

    public static void logToSlack(String message, FileConfiguration config, String time, String pluginName){
        //TO CREATE THE FIRST LINER SUMMARY
        String[] firstLine = message.split("\n", 3);
        String summary = "A " + firstLine[0] + firstLine[1];
        summary = summary.replaceAll(Pattern.quote("\t")," ");

        //TO CREATE THE CHANNEL NAME
        String channel = config.getBoolean("slack.pm.enabled") ?
                "@" + config.getString("slack.pm.user").toLowerCase()
                : null;

        //PUTTING IT ALL TOGETHER
        SlackMessage msg = new SlackMessage(summary)
                .addAttachments(
                        new SlackAttachment()
                                .setFallback(summary)
                                .setText("```" + message.replaceAll(Pattern.quote("\n"), "") + "```")
                                .setFields(new ArrayList<>(Arrays.asList(
                                        new SlackField(true, "Server", "_" + config.getString("slack.server-name") + "_"),
                                        new SlackField(true, "Version", "_"
                                                + ErrorHandling.getInstance().getServer().getBukkitVersion() + "_"),
                                        new SlackField(true, "Date", "_" + time + "_"),
                                        new SlackField(true, "Plugin", "_" + pluginName + "_"))))
                                .setColor("danger")
                                .addAllowedMarkdown("text")
                                .addAllowedMarkdown("fields")
                                .addTitle("Stack Trace"))
                .setChannel(channel);
        ErrorHandling.getInstance().getApi().call(msg);
    }

}
