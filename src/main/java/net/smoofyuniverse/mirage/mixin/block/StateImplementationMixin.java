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

package net.smoofyuniverse.mirage.mixin.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer.StateImplementation;
import net.minecraft.block.state.IBlockState;
import net.smoofyuniverse.mirage.impl.internal.InternalBlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StateImplementation.class)
public class StateImplementationMixin implements InternalBlockState {
	@Shadow
	@Final
	private ImmutableMap<IProperty<?>, Comparable<?>> properties;

	@Shadow
	@Final
	private Block block;

	private boolean isOpaqueCube;

	private boolean cacheHashCode = true;
	private int hashCode;

	/**
	 * This method is called after all blocks have been registered to avoid errors with some mods
	 */
	@Override
	public void optimizeExpositionCheck() {
		this.isOpaqueCube = this.block.isOpaqueCube((IBlockState) this);
	}

	@Override
	public boolean isOpaque() {
		return this.isOpaqueCube;
	}

	/**
	 * @author Yeregorix
	 * @reason Improve HashSet and HashMap performance
	 */
	@Override
	@Overwrite
	public int hashCode() {
		if (this.cacheHashCode) {
			this.hashCode = this.properties.hashCode();
			this.cacheHashCode = false;
		}
		return this.hashCode;
	}
}
