package com.minecave.errors.listener;

import com.minecave.errors.ErrorHandling;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;


/**
 * Created by Carter on 7/29/2015.
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
            if(ErrorHandling.getInstance().getThreshold().isHalted(this.pluginN) &&
                    !ErrorHandling.getInstance().getConfig().getBoolean("log.threshold.enabled")){
                return;
            }
            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            DateFormat df = new SimpleDateFormat("HH:mm:ss @ MM-dd-yyyy");
            String time = df.format(new Date());

            if(ErrorHandling.getInstance().getConfig().getBoolean("log.enabled")) {
                String folderpath = ErrorHandling.getInstance().getDataFolder() + File.separator + pluginN;
                File folder = new File(folderpath);
                if (!folder.exists())
                    folder.mkdir();

                File file = new File(folderpath + File.separator + date + ".log");
                if (!file.exists())
                    file.createNewFile();

                FileWriter fileWriter = new FileWriter(file, true);
                PrintWriter printWriter = new PrintWriter(fileWriter);

                printWriter.println("");
                printWriter.println(time);
                record.getThrown().printStackTrace(printWriter);
                printWriter.close();
                fileWriter.close();
            }
            if(ErrorHandling.getInstance().getConfig().getBoolean("slack.enabled")){
                ErrorHandling.getInstance().send(ExceptionUtils.getStackTrace(record.getThrown()), time, pluginN);
            }
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

