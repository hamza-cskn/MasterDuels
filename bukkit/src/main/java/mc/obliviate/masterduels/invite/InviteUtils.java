package mc.obliviate.masterduels.invite;

import mc.obliviate.masterduels.api.kit.IKit;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.List;

public class InviteUtils {

	public static void sendInviteMessage(final Invite invite, final List<String> inviteTextList) {
		final Player inviter = invite.getInviter();
		final Player target = invite.getTarget();

		final IKit kit = invite.getGameCreator().getBuilder().getKit();

		for (String inviteText : inviteTextList) {
			inviteText = inviteText + " ";
			inviteText = MessageUtils.applyPlaceholders(inviteText, new PlaceholderUtil().add("{kit}", kit != null ? kit.getKitName() : MessageUtils.parseColor(MessageUtils.getMessage("game-creator.none-kit-name"))).add("{inviter}", inviter.getName()).add("{expire-time}", invite.getExpireTime() + ""));
			inviteText = MessageUtils.parseColor(inviteText);

			if (inviteText.contains("{accept-button}") && inviteText.contains("{decline-button}")) {
				final TextComponent acceptButton = new TextComponent(MessageUtils.parseColor(MessageUtils.getMessage("invite.button.accept-button.text")));
				acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.parseColor(MessageUtils.getMessage("invite.button.accept-button.hover"))).create()));
				acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept"));

				final TextComponent declineButton = new TextComponent(MessageUtils.parseColor(MessageUtils.getMessage("invite.button.decline-button.text")));
				declineButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.parseColor(MessageUtils.getMessage("invite.button.decline-button.hover"))).create()));
				declineButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel decline"));

				final String[] strings = inviteText.split("\\{accept-button}|\\{decline-button}");

				target.spigot().sendMessage(new TextComponent(strings[0]), acceptButton, new TextComponent(strings[1]), declineButton, new TextComponent(strings[2]));
			} else {
				target.spigot().sendMessage(new TextComponent(inviteText));
			}
		}
	}

}
