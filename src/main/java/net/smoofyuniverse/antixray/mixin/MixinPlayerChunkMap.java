/*
 * Copyright (c) 2018 Hugo Dupanloup (Yeregorix)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.smoofyuniverse.antixray.mixin;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.world.WorldServer;
import net.smoofyuniverse.antixray.impl.internal.InternalChunkMap;
import net.smoofyuniverse.antixray.impl.internal.InternalWorld;
import net.smoofyuniverse.antixray.impl.network.dynamism.PlayerDynamismManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(PlayerChunkMap.class)
public class MixinPlayerChunkMap implements InternalChunkMap {

	@Shadow
	@Final
	private WorldServer world;
	@Shadow
	@Final
	private List<EntityPlayerMP> players;

	private Map<UUID, PlayerDynamismManager> dynamismManagers = new HashMap<>();

	@Inject(method = "addPlayer", at = @At("HEAD"))
	public void onAddPlayer(EntityPlayerMP player, CallbackInfo ci) {
		if (isDynamismEnabled()) {
			PlayerDynamismManager manager = new PlayerDynamismManager(player.getUniqueID());
			manager.setCenter(((Player) player).getPosition().toInt());
			this.dynamismManagers.put(manager.playerId, manager);
		}
	}

	@Override
	public boolean isDynamismEnabled() {
		return ((InternalWorld) this.world).getView().isDynamismEnabled();
	}

	@Nullable
	@Override
	public PlayerDynamismManager getDynamismManager(UUID playerId) {
		return this.dynamismManagers.get(playerId);
	}

	@Inject(method = "removePlayer", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(Ljava/lang/Object;)Z"))
	public void onRemovePlayer(EntityPlayerMP player, CallbackInfo ci) {
		if (isDynamismEnabled())
			this.dynamismManagers.remove(player.getUniqueID());
	}

	@Inject(method = "updateMovingPlayer", at = @At("RETURN"))
	public void onUpdateMovingPlayer(EntityPlayerMP player, CallbackInfo ci) {
		if (!isDynamismEnabled())
			return;

		PlayerDynamismManager manager = this.dynamismManagers.get(player.getUniqueID());
		if (manager != null)
			manager.update((Player) player);
	}
}
