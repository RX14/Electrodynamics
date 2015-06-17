package com.calclavia.edx.optics.grid

import java.util

import com.calclavia.edx.core.EDX
import nova.core.component.Updater
import nova.core.world.World

import scala.collection.convert.wrapAll._

/**
 * A grid that manages all waves produced in the world
 * @author Calclavia
 */
object OpticGrid {

	//Max laser render distance
	val maxDistance = 100

	//Minimum energy for laser
	val minPower = 100d
	val maxPower = 20000d

	val minEnergyToMine = 10000d
	val maxEnergyToMine = 500000d
	val minBurnEnergy = minEnergyToMine

	private val grids = new util.WeakHashMap[World, OpticGrid]

	def apply(world: World): OpticGrid = {
		if (!grids.containsKey(world)) {
			grids += (world -> new OpticGrid(world))
		}

		return grids(world)
	}

	def clear() = grids.clear()
}

class OpticGrid(val world: World) extends Updater {

	var sources = Set.empty[Beam]

	var handlers = Set.empty[OpticHandler]

	private var graphChanged = true

	EDX.syncTicker.add(this)

	def register(handler: OpticHandler) {
		handlers += handler
	}

	def unregister(handler: OpticHandler) {
		handlers -= handler
	}

	/**
	 * Creates a beam source point.
	 * Call server-side only.
	 */
	def create(beam: Beam) {
		sources.synchronized {
			if (beam.power > OpticGrid.minPower) {
				sources += beam
				println("Created beam" + sources)
				graphChanged = true
			}
		}
	}

	/**
	 * Destroys a beam source point
	 * Call server-side only.
	 * @param beam The beam to remove
	 */
	def destroy(beam: Beam) {
		sources.synchronized {
			if (sources.contains(beam)) {
				sources -= beam
				println("Destroyed beam")
				graphChanged = true
			} else {
				EDX.logger.error("Attempt to remove node that does not exist in wave grid.")
			}
		}
	}

	var timer = 0d

	override def update(deltaTime: Double) {
		super.update(deltaTime)

		timer += deltaTime

		if (timer >= 0.3) {
			timer = 0

			if (!EDX.clientManager.isPaused) {
				sources.synchronized {
					handlers.foreach(_.reset())
					sources.foreach(_.update())

					if (EDX.network.isServer) {
						println("Optic sources " + sources.size + " this " + this)
						//Update client
						if (graphChanged) {
							EDX.network.sync(this)
							graphChanged = false
						}
						//TODO: Remove
						graphChanged = true
					}
				}
			}
		}
	}
}
