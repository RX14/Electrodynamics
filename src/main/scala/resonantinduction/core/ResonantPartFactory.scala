package resonantinduction.core

import codechicken.multipart.MultiPartRegistry.IPartFactory
import codechicken.multipart.{MultiPartRegistry, TMultiPart}

import scala.collection.mutable

/**
 * @author Calclavia
 */
object ResonantPartFactory extends IPartFactory
{
  val prefix = Reference.prefix

  private val partMap = mutable.Map.empty[String, Class[_<:TMultiPart]]

  def register(part: Class[_<:TMultiPart])
  {
    partMap.put(prefix + part.getClass.getSimpleName, part)
  }

  def init() = MultiPartRegistry.registerParts(this, partMap.keys.toArray)

  def createPart(name: String, client: Boolean): TMultiPart = partMap(name).newInstance()
}