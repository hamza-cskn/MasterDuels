package mc.obliviate.masterduels.utils;

import mc.obliviate.masterduels.MasterDuels;
import org.bukkit.Bukkit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class Logger {

    private static boolean debugModeEnabled = false;
    private static DebugPart debugPart = null;

    public static void debug(DebugPart part, String message) {
        if (!debugModeEnabled) return;
        if (part.equals(debugPart)) return;
        debug(message);
    }

    public static void debug(String message) {
        if (!debugModeEnabled) return;
        Bukkit.getLogger().info("[MasterDuels] [DEBUG] " + fixLength(message));
        Logger.writeLog("console-logs", "[MasterDuels] [DEBUG] " + message);
    }

    public static void severe(String message) {
        Bukkit.getLogger().severe("[MasterDuels] " + fixLength(message));
        Logger.writeLog("console-logs", "[MasterDuels] [SEVERE] " + message);
    }

    public static void warn(String message) {
        Bukkit.getLogger().warning("[MasterDuels] " + fixLength(message));
        Logger.writeLog("console-logs", "[MasterDuels] [WARN] " + message);
    }

    public static void error(String message) {
        Bukkit.getConsoleSender().sendMessage("[MasterDuels] [ERROR] " + fixLength(message));
        Logger.writeLog("console-logs", "[MasterDuels] [ERROR] " + message);
    }

    private static String fixLength(String string) {
        if (string.length() > 500) {
            return string.substring(0, 500) + "...";
        }
        return string;
    }

    public static DebugPart getDebugPart() {
        return debugPart;
    }

    public static void setDebugPart(DebugPart debugPart) {
        Logger.debugPart = debugPart;
    }

    public static void setDebugModeEnabled(boolean debugModeEnabled) {
        Logger.debugModeEnabled = debugModeEnabled;
    }

    public static boolean isDebugModeEnabled() {
        return debugModeEnabled;
    }

    public enum DebugPart {
        GAME
    }

    public static void writeLog(String fileName, String text) {
        File file = new File(MasterDuels.getInstance().getDataFolder().getPath() + File.separator + fileName + ".log");
        try {
            Writer writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8)));

            writer.write(text + "\n");
            writer.flush();
            writer.close();
        } catch (IOException eb) {
            eb.printStackTrace();
        }
    }

}
