package mc.obliviate.masterduels.data.database.user;

import mc.obliviate.masterduels.user.IUser;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserDatabase {

	default void connect() {

	}

	default void disconnect() {

	}

	CompletableFuture<IUser> loadUser(UUID uuid);

	CompletableFuture<Void> saveUser(IUser user);

}
