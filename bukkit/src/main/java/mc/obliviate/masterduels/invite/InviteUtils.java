package mc.obliviate.masterduels.invite;

import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import mc.obliviate.util.placeholder.PlaceholderUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class InviteUtils {

	public static void sendInviteMessage(final Invite invite, final ConfigurationSection section) {
		final Player sender = Bukkit.getPlayer(invite.getSenderUniqueId());
		final Player target = Bukkit.getPlayer(invite.getRecipientUniqueId());

		final Kit kit = invite.getKit(); //fixme

		for (String inviteText : section.getStringList("text")) {
			inviteText = inviteText + " ";
			inviteText = MessageUtils.applyPlaceholders(inviteText, new PlaceholderUtil().add("{kit}", kit != null ? kit.getKitName() : MessageUtils.parseColor(MessageUtils.getMessage("kit.none-kit-name"))).add("{inviter}", sender.getName()).add("{expire-time}", TimerUtils.formatTimeUntilThenAsTimer(invite.getExpireOutTime()) + ""));
			inviteText = MessageUtils.parseColor(inviteText);

			if (inviteText.contains("{accept-button}") && inviteText.contains("{decline-button}")) {
				final TextComponent acceptButton = new TextComponent(MessageUtils.parseColor(section.getString("button.accept-button.text")));
				acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.parseColor(section.getString("button.accept-button.hover"))).create()));
				acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept"));

				final TextComponent declineButton = new TextComponent(MessageUtils.parseColor(section.getString("button.decline-button.text")));
				declineButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.parseColor(section.getString("button.decline-button.hover"))).create()));
				declineButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel decline"));

				final String[] strings = inviteText.split("\\{accept-button}|\\{decline-button}");

				target.spigot().sendMessage(new TextComponent(strings[0]), acceptButton, new TextComponent(strings[1]), declineButton, new TextComponent(strings[2]));
			} else {
				target.spigot().sendMessage(new TextComponent(inviteText));
			}
		}
	}

}
