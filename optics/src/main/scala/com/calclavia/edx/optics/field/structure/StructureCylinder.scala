package com.calclavia.edx.optics.field.structure

import com.resonant.core.structure.Structure
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D

/**
 * @author Calclavia
 */
class StructureCylinder extends Structure {

	private val radiusExpansion = 0
	private val height = 2
	private val radius = 1

	/**
	 * Gets the equation that define the 3D surface in standard form.
	 * The transformation should be default.
	 * @return The result of the equation. Zero if the position satisfy the equation.
	 */
	override def surfaceEquation(position: Vector3D): Double = {
		if ((position.getY() == 0 || position.getY() == height - 1) && (position.getX() * position.getX() + position.getZ() * position.getZ() + radiusExpansion) <= (radius * radius)) {
			return 1
		}

		if ((position.getX() * position.getX() + position.getZ() * position.getZ() + radiusExpansion) <= (radius * radius) && (position.getX() * position.getX() + position.getZ() * position.getZ() + radiusExpansion) >= ((radius - 1) * (radius - 1))) {
			return 1
		}
		return 0
	}

	/**
	 * Gets the equation that define the 3D volume in standard form.
	 * The transformation should be default.
	 * @return The result of the equation. Zero if the position satisfy the equation.
	 */
	override def volumeEquation(position: Vector3D): Double = {
		if (position.getX() * position.getX() + position.getZ() * position.getZ() + radiusExpansion <= radius * radius) {
			return 1
		}

		return 0
	}

	override def getID: String = "cylinder"
}
