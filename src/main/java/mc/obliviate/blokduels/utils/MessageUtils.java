package mc.obliviate.blokduels.utils;

import mc.obliviate.blokduels.utils.placeholder.InternalPlaceholder;
import mc.obliviate.blokduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MessageUtils {

	private static YamlConfiguration messageConfig;
	private static String prefix;

	public static YamlConfiguration getMessageConfig() {
		return messageConfig;
	}

	public static void setMessageConfig(final YamlConfiguration messageConfig) {
		MessageUtils.messageConfig = messageConfig;
		MessageUtils.prefix = parseColor(messageConfig.getString("prefix"));
	}

	public static String getMessage(final String node) {
		final String msg = messageConfig.getString(node);
		if (msg == null) {
			return "No Message Found: " + node;
		} else if (msg.equalsIgnoreCase("DISABLED")) {
			return null;
		}
		return msg;
	}

	public static void sendMessage(final CommandSender player, final String configNode) {
		String message = getMessage(configNode);
		if (message == null) return;
		message = parseColor(message);
		player.sendMessage(prefix + message);
	}

	public static void sendMessageList(final CommandSender player, final String configNode) {
		final List<String> messages = parseColor(messageConfig.getStringList(configNode));
		for (final String str : messages) {
			player.sendMessage(str);
		}
	}

	public static void sendMessage(final Player player, final String configNode, final PlaceholderUtil placeholderUtil) {
		String message = getMessage(configNode);
		if (message == null) return;
		player.sendMessage(prefix + parseColor(applyPlaceholders(message, placeholderUtil)));
	}

	public static String applyPlaceholders(String message, final PlaceholderUtil placeholderUtil) {
		for (final InternalPlaceholder placeholder : placeholderUtil.getPlaceholders()) {
			message = message.replace(placeholder.getPlaceholder(), placeholder.getValue());
		}
		return message;
	}

	public static String parseColor(final String string) {
		if (string == null) return null;
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static List<String> parseColor(final List<String> stringList) {
		if (stringList == null) return null;
		final List<String> result = new ArrayList<>();
		for (String str : stringList) {
			result.add(ChatColor.translateAlternateColorCodes('&', str));
		}
		return result;
	}

	public static List<String> listReplace(final List<String> stringList, final String search, final String replace) {
		if (stringList == null) return null;
		final List<String> result = new ArrayList<>();
		for (String str : stringList) {
			result.add(str.replace(search, replace));
		}
		return result;
	}

	public static int getPercentage(final double total, final double progress) {
		try {
			return (int) (progress / (total / 100d));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static String getProgressBar(final int completed, final int total, final String got, final String missing) {
		final StringBuilder points = new StringBuilder();
		for (int i = 0; i + 1 <= total; i++) {
			if (i >= completed) {
				points.append(missing);
			} else {
				points.append(got);
			}
		}
		return points.toString();
	}

	public static String convertMode(final int size, int amount) {
		final StringBuilder sb = new StringBuilder();
		for (; amount > 0; amount--) {
			sb.append(size);
			if (amount != 1) {
				sb.append("v");
			}
		}
		return sb.toString();
	}

	public static double getFirstDigits(final double number, final int digitAmount) {
		final double multiple = Math.pow(10, digitAmount);
		return (((int) (number * multiple)) / multiple);
	}

}
