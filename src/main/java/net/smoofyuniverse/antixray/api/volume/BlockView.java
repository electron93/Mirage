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

package net.smoofyuniverse.antixray.api.volume;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.extent.MutableBlockVolume;

/**
 * A BlockView is a MutableBlockVolume associated with immutable BlockStorage. This object is used to represent a client-side BlockVolume.
 */
public interface BlockView extends MutableBlockVolume {

	/**
	 * @return The BlockStorage which is associated with this BlockView
	 */
	BlockStorage getStorage();

	/**
	 * @param minX The X minimum position
	 * @param minY The Y minimum position
	 * @param minZ The Z minimum position
	 * @param maxX The X maximum position
	 * @param maxY The Y maximum position
	 * @param maxZ The Z maximum position
	 * @throws PositionOutOfBoundsException if one of the two positions is not contained in the volume
	 * @throws IllegalArgumentException     if the two positions does not defines a valid area
	 */
	default void checkBlockArea(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		checkBlockPosition(minX, minY, minZ);
		checkBlockPosition(maxX, maxY, maxZ);

		if (minX > maxX || minY > maxY || minZ > maxZ)
			throw new IllegalArgumentException("Invalid area");
	}

	/**
	 * @param x The X position
	 * @param y The Y position
	 * @param z The Z position
	 * @throws PositionOutOfBoundsException if the position is not contained in the volume
	 */
	default void checkBlockPosition(int x, int y, int z) {
		if (!containsBlock(x, y, z))
			throw new PositionOutOfBoundsException(new Vector3i(x, y, z), getBlockMin(), getBlockMax());
	}

	/**
	 * @return Whether dynamic obfuscation is enabled in this volume
	 */
	boolean isDynamismEnabled();

	/**
	 * Sets the dynamism distance of the given position
	 *
	 * @param pos      The position
	 * @param distance The distance of dynamism, between 0 and 15
	 */
	default void setDynamism(Vector3i pos, int distance) {
		setDynamism(pos.getX(), pos.getY(), pos.getZ(), distance);
	}

	/**
	 * Sets the dynamism distance of the given position
	 *
	 * @param x The X position
	 * @param y The Y position
	 * @param z The Z position
	 * @param distance The distance of dynamism, between 0 and 15
	 */
	void setDynamism(int x, int y, int z, int distance);

	/**
	 * @param pos The position
	 * @return The distance of dynamism, between 0 and 15
	 */
	default int getDynamism(Vector3i pos) {
		return getDynamism(pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * @param x The X position
	 * @param y The Y position
	 * @param z The Z position
	 * @return The distance of dynamism, between 0 and 15
	 */
	int getDynamism(int x, int y, int z);

	/**
	 * Checks if the block at the given position is exposed to the view of normal users.
	 * This can be done by checking the FullBlockSelectionBoxProperty of each surrounding block but this method is optimized for performances.
	 *
	 * @param pos The position
	 * @return Whether the block is exposed
	 */
	default boolean isExposed(Vector3i pos) {
		return isExposed(pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * Checks if the block at the given position is exposed to the view of normal users.
	 * This can be done by checking the FullBlockSelectionBoxProperty of each surrounding block but this method is optimized for performances.
	 *
	 * @param x The X position
	 * @param y The Y position
	 * @param z The Z position
	 * @return Whether the block is exposed
	 */
	boolean isExposed(int x, int y, int z);

	/**
	 * Deobfuscates a single block at the given position.
	 *
	 * @param pos The position
	 * @return Whether the block has been deobfuscated
	 */
	default boolean deobfuscate(Vector3i pos) {
		return deobfuscate(pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * Deobfuscates a single block at the given position.
	 *
	 * @param x The X position
	 * @param y The Y position
	 * @param z The Z position
	 * @return Whether the block has been deobfuscated
	 */
	boolean deobfuscate(int x, int y, int z);

	/**
	 * Deobfuscates all blocks around the given position according to the radius.
	 *
	 * @param pos    The position
	 * @param radius The radius
	 * @param silentFail Enable or disable silent fail
	 * @throws IllegalStateException if affected chunks are not loaded and silent fail is disabled
	 */
	default void deobfuscateSurrounding(Vector3i pos, int radius, boolean silentFail) {
		deobfuscateSurrounding(pos.getX(), pos.getY(), pos.getZ(), radius, silentFail);
	}

	/**
	 * Deobfuscates all blocks around the given position according to the radius.
	 *
	 * @param x      The X position
	 * @param y      The Y position
	 * @param z      The Z position
	 * @param radius The radius
	 * @param silentFail Enable or disable silent fail
	 * @throws IllegalStateException if affected chunks are not loaded and silent fail is disabled
	 */
	default void deobfuscateSurrounding(int x, int y, int z, int radius, boolean silentFail) {
		checkBlockPosition(x, y, z);

		if (radius < 0)
			throw new IllegalArgumentException("Negative radius");

		if (radius == 0)
			deobfuscate(x, y, z);
		else {
			Vector3i min = getBlockMin(), max = getBlockMax();
			deobfuscateArea(Math.max(x - radius, min.getX()), Math.max(y - radius, min.getY()), Math.max(z - radius, min.getZ()),
					Math.min(x + radius, max.getX()), Math.min(y + radius, max.getY()), Math.min(z + radius, max.getZ()), silentFail);
		}
	}

	/**
	 * Deobfuscates all blocks in the given area.
	 *
	 * @param minX The X minimum position
	 * @param minY The Y minimum position
	 * @param minZ The Z minimum position
	 * @param maxX The X maximum position
	 * @param maxY The Y maximum position
	 * @param maxZ The Z maximum position
	 * @param silentFail Enable or disable silent fail
	 * @throws IllegalStateException if affected chunks are not loaded and silent fail is disabled
	 */
	void deobfuscateArea(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean silentFail);

	/**
	 * Deobfuscates all blocks in the given area.
	 *
	 * @param min The minimum position
	 * @param max The maximum position
	 * @param silentFail Enable or disable silent fail
	 * @throws IllegalStateException if affected chunks are not loaded and silent fail is disabled
	 */
	default void deobfuscateArea(Vector3i min, Vector3i max, boolean silentFail) {
		deobfuscateArea(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ(), silentFail);
	}

	/**
	 * Reobfuscates all blocks around the given position according to the radius.
	 *
	 * @param pos    The position
	 * @param radius The radius
	 * @param silentFail Enable or disable silent fail
	 * @throws IllegalStateException if affected chunks are not fully obfuscated and silent fail is disabled
	 */
	default void reobfuscateSurrounding(Vector3i pos, int radius, boolean silentFail) {
		reobfuscateSurrounding(pos.getX(), pos.getY(), pos.getZ(), radius, silentFail);
	}

	/**
	 * Reobfuscates all blocks around the given position according to the radius.
	 *
	 * @param x      The X position
	 * @param y      The Y position
	 * @param z      The Z position
	 * @param radius The radius
	 * @param silentFail Enable or disable silent fail
	 * @throws IllegalStateException if affected chunks are not fully obfuscated and silent fail is disabled
	 */
	default void reobfuscateSurrounding(int x, int y, int z, int radius, boolean silentFail) {
		checkBlockPosition(x, y, z);

		if (radius < 0)
			throw new IllegalArgumentException("Negative radius");

		Vector3i min = getBlockMin(), max = getBlockMax();
		reobfuscateArea(Math.max(x - radius, min.getX()), Math.max(y - radius, min.getY()), Math.max(z - radius, min.getZ()),
				Math.min(x + radius, max.getX()), Math.min(y + radius, max.getY()), Math.min(z + radius, max.getZ()), silentFail);
	}

	/**
	 * Reobfuscates all blocks in the given area.
	 *
	 * @param minX The X minimum position
	 * @param minY The Y minimum position
	 * @param minZ The Z minimum position
	 * @param maxX The X maximum position
	 * @param maxY The Y maximum position
	 * @param maxZ The Z maximum position
	 * @param silentFail Enable or disable silent fail
	 * @throws IllegalStateException if affected chunks are not fully obfuscated and silent fail is disabled
	 */
	void reobfuscateArea(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean silentFail);

	/**
	 * Reobfuscates all blocks in the given area.
	 *
	 * @param min The minimum position
	 * @param max The maximum position
	 * @param silentFail Enable or disable silent fail
	 * @throws IllegalStateException if affected chunks are not fully obfuscated and silent fail is disabled
	 */
	default void reobfuscateArea(Vector3i min, Vector3i max, boolean silentFail) {
		reobfuscateArea(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ(), silentFail);
	}
}
