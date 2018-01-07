/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Thomas Vanmellaerts, 2018 Hugo Dupanloup (Yeregorix)
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

package com.thomas15v.noxray.api;

import com.thomas15v.noxray.config.WorldConfig;
import com.thomas15v.noxray.modifications.internal.InternalChunk;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;

/**
 * Represent the world viewed for the network (akka online players)
 */
public class NetworkWorld {
	private WorldConfig config;
	private final World world;

	public NetworkWorld(World w) {
		this.world = w;
	}

	public void sendBlockChanges() {
		for (Chunk c : this.world.getLoadedChunks())
			((InternalChunk) c).sendBlockChanges();
	}

	public WorldConfig getConfig() {
		return this.config;
	}

	public World getWorld() {
		return this.world;
	}
}
