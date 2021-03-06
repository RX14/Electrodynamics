package com.calclavia.edx.optics.field.module

import com.calclavia.edx.optics.GraphFrequency
import com.calclavia.edx.optics.api.machine.Projector
import com.calclavia.edx.optics.api.modules.Module.ProjectState
import com.calclavia.edx.optics.component.ItemModule
import com.calclavia.edx.optics.field.BlockProjector
import nova.core.block.Block
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D

/**
 * The fusion module attempts to fuse a force field withPriority another.
 * This is done by checking all other force fields withPriority THE SAME FREQUENCY,
 * and trying to fuse them together. 
 * @author Calclavia
 */
class ItemModuleFusion extends ItemModule {
	setCost(1f)
	setMaxCount(1)

	override def getID: String = "moduleFusion"

	override def onProject(projector: Projector, position: Vector3D): ProjectState = {
		if (GraphFrequency.instance
			.get(projector.getFrequency)
			.view
			.collect { case proj: BlockProjector => proj }
			.filter(_.world() == projector.asInstanceOf[Block].world())
			.filter(_.isActive)
			.filter(_.getShapeItem != null)
			.exists(_.isInField(position))) {
			return ProjectState.skip
		}

		return ProjectState.pass
	}
}