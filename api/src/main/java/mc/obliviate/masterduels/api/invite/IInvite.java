package mc.obliviate.masterduels.api.invite;

import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface IInvite {

	int getExpireTime();

	void onExpire();

	String getFormattedExpireTimeLeft() ;

	boolean isExpired();

	Player getInviter();

	Player getTarget();

	void setResult(boolean answer);

	Consumer<InviteResult> getResponse();

	void onResponse(Consumer<InviteResult> response);
}
