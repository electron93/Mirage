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

package net.smoofyuniverse.mirage.api.modifier;

import com.google.common.collect.ImmutableList;
import net.smoofyuniverse.mirage.Mirage;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * An AdditionalCatalogRegistryModule used to get or register ChunkModifiers
 */
public class ChunkModifierRegistryModule implements AdditionalCatalogRegistryModule<ChunkModifier> {
	private static final ChunkModifierRegistryModule INSTANCE = new ChunkModifierRegistryModule();
	private static final String DEFAULT_PREFIX = Mirage.get().getContainer().getId() + ":";

	private final Map<String, ChunkModifier> modifiers = new HashMap<>();

	private ChunkModifierRegistryModule() {}

	@Override
	public Optional<ChunkModifier> getById(String id) {
		id = id.toLowerCase();
		return Optional.ofNullable(this.modifiers.get(id.indexOf(':') == -1 ? (DEFAULT_PREFIX + id) : id));
	}

	@Override
	public Collection<ChunkModifier> getAll() {
		return ImmutableList.copyOf(this.modifiers.values());
	}

	@Override
	public void registerDefaults() {
		register(ChunkModifiers.EMPTY);
		register(ChunkModifiers.HIDEALL);
		register(ChunkModifiers.OBVIOUS);
		register(ChunkModifiers.RANDOM);
		register(ChunkModifiers.BEDROCK);
		register(ChunkModifiers.FAKEGEN);
	}

	private void register(ChunkModifier modifier) {
		this.modifiers.put(modifier.getId(), modifier);
	}

	@Override
	public void registerAdditionalCatalog(ChunkModifier modifier) {
		if (this.modifiers.containsKey(modifier.getId()))
			throw new IllegalArgumentException("Cannot register an already registered ChunkModifier: " + modifier.getId());
		register(modifier);
	}

	/**
	 * @return The default ChunkModifierRegistryModule instance
	 */
	public static ChunkModifierRegistryModule get() {
		return INSTANCE;
	}
}
