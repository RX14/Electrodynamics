package com.calclavia.edx.optics.field.shape

import com.calclavia.edx.optics.content.OpticsTextures
import com.calclavia.edx.optics.field.structure.StructurePyramid
import com.resonant.core.structure.Structure
import nova.core.render.model.{Model, Vertex}
import nova.scala.wrapper.FunctionalWrapper._
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D

class ItemShapePyramid extends ItemShape {
	override def getID: String = "modePyramid"

	override def getStructure: Structure = new StructurePyramid

	renderer.setOnRender(
		(model: Model) => {
			val height = 0.5f
			val width = 0.3f
			val uvMaxX = 2
			val uvMaxY = 2
			val translation = new Vector3D(0, -0.4, 0)
			model.matrix.rotate(new Vector3D(0, 0, 1), Math.PI)
			val face = model.createFace()
			face.drawVertex(new Vertex(0 + translation.getX(), 0 + translation.getY(), 0 + translation.getZ(), 0, 0))
			face.drawVertex(new Vertex(-width + translation.getX(), height + translation.getY(), -width + translation.getZ(), -uvMaxX, -uvMaxY))
			face.drawVertex(new Vertex(-width + translation.getX(), height + translation.getY(), width + translation.getZ(), -uvMaxX, uvMaxY))
			face.drawVertex(new Vertex(width + translation.getX(), height + translation.getY(), width + translation.getZ(), uvMaxX, uvMaxY))
			face.drawVertex(new Vertex(width + translation.getX(), height + translation.getY(), -width + translation.getZ(), uvMaxX, -uvMaxY))
			face.drawVertex(new Vertex(-width + translation.getX(), height + translation.getY(), -width + translation.getZ(), -uvMaxX, -uvMaxY))
			model.drawFace(face)
			model.bindAll(OpticsTextures.hologram)
		}
	)
}