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

package net.smoofyuniverse.noxray.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.block.BlockState;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Options {
	public final Set<BlockState> oresSet;
	public final List<BlockState> oresList;
	public final BlockState ground;
	public final float density;
	public final int ores;

	public Options(Collection<BlockState> ores, BlockState ground, float density) {
		this.oresSet = ImmutableSet.copyOf(ores);
		this.oresList = ImmutableList.copyOf(ores);
		this.ground = ground;
		this.density = density;
		this.ores = ores.size();
	}

	public BlockState randomBlock(Random r) {
		return (this.ores == 0 || (this.density != 1 && r.nextFloat() > this.density)) ? this.ground : this.oresList.get(r.nextInt(this.ores));
	}
}