/*
 * Copyright (c) 2018-2019 Hugo Dupanloup (Yeregorix)
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

package net.smoofyuniverse.mirage.mixin.world;

import com.flowpowered.math.vector.Vector2i;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.smoofyuniverse.mirage.api.volume.ChunkStorage;
import net.smoofyuniverse.mirage.impl.internal.InternalChunk;
import net.smoofyuniverse.mirage.impl.internal.InternalWorld;
import net.smoofyuniverse.mirage.impl.network.NetworkWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

@Mixin(World.class)
public abstract class WorldMixin implements InternalWorld {
	@Shadow
	protected IChunkProvider chunkProvider;

	@Shadow
	@Final
	public boolean isRemote;

	@Override
	public NetworkWorld getView() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isExposed(int x, int y, int z) {
		InternalChunk chunk = getChunk(x >> 4, z >> 4);
		return chunk != null && chunk.isExposed(x, y, z);
	}

	@Override
	public Vector2i getLightLevels(int x, int y, int z) {
		InternalChunk chunk = getChunk(x >> 4, z >> 4);
		return chunk == null ? Vector2i.ZERO : chunk.getLightLevels(x, y, z);
	}

	@Override
	public int getHighestY(int x, int z) {
		InternalChunk chunk = getChunk(x >> 4, z >> 4);
		return chunk == null ? 0 : chunk.getHighestY(x, z);
	}

	@Nullable
	@Override
	public InternalChunk getChunk(int x, int z) {
		return (InternalChunk) this.chunkProvider.getLoadedChunk(x, z);
	}

	@Override
	public boolean isChunkLoaded(int x, int z) {
		return getChunk(x, z) != null;
	}

	@Override
	public Optional<ChunkStorage> getChunkStorage(int x, int y, int z) {
		return Optional.ofNullable(getChunk(x, z));
	}


	@Override
	public Optional<ChunkStorage> getChunkStorageAt(int x, int y, int z) {
		return getChunkStorage(x >> 4, 0, z >> 4);
	}

	@Override
	public Collection<InternalChunk> getLoadedChunkStorages() {
		if (this.isRemote)
			return ImmutableList.of();
		return ImmutableList.copyOf((Collection) ((ChunkProviderServer) this.chunkProvider).getLoadedChunks());
	}
}
