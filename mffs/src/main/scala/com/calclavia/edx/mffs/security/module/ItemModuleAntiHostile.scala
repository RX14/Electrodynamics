package com.calclavia.edx.mffs.security.module

import java.util

import com.calclavia.edx.mffs.api.machine.Projector
import nova.core.component.misc.Damageable
import nova.core.util.transform.vector.Vector3i

class ItemModuleAntiHostile extends ItemModuleDefense {

	override def onCreateField(projector: Projector, field: util.Set[Vector3i]): Boolean = {
		val entities = getEntitiesInField(projector)

		//Check entity IDs.
		entities.view
			.filter(entity => entity.has(classOf[Damageable]) /* && entity.isInstanceOf[IMob] && !entity.isInstanceOf[INpc]*/)
			.map(_.get(classOf[Damageable]))
			.foreach(_.damage(20))

		return false
	}

	override def getID: String = "moduleAntiHostile"
}