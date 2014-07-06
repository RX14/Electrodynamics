package mffs.field.module

import java.util.Set

import mffs.base.ItemModule
import net.minecraft.tileentity.TileEntity
import resonant.api.mffs.IFieldInteraction
import universalelectricity.core.transform.vector.Vector3

import scala.collection.convert.wrapAll._

class ItemModuleDome(id: Int) extends ItemModule(id, "moduleDome")
{
  setMaxStackSize(1)

  override def onCalculate(projector: IFieldInteraction, fieldBlocks: Set[Vector3])
  {
    val absoluteTranslation = new Vector3(projector.asInstanceOf[TileEntity]) + projector.getTranslation
    val newField = fieldBlocks.par.filter(_.y > absoluteTranslation.y).seq
    fieldBlocks.clear()
    fieldBlocks.addAll(newField)
  }
}