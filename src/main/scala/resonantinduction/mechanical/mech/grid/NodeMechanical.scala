package resonantinduction.mechanical.mech.grid

import resonant.api.grid.INodeProvider
import resonant.lib.grid.GridNode
import resonant.lib.grid.node.NodeGrid
import resonant.lib.transform.vector.IVectorWorld
import resonantinduction.core.interfaces.TMechanicalNode
import resonantinduction.core.prefab.node.TMultipartNode

import scala.beans.BeanProperty

/**
 * Prefab node for the mechanical system used by almost ever mechanical object in Resonant Induction. Handles connections to other tiles, and shares power with them
 *
 * @author Calclavia, Darkguardsman
 */
class NodeMechanical(parent: INodeProvider) extends NodeGrid[NodeMechanical](parent) with TMultipartNode[NodeMechanical] with TMechanicalNode with IVectorWorld
{
  protected[grid] var _torque = 0D
  protected[grid] var _angularVelocity = 0D

  /**
   * Gets the angular velocity of the mechanical device from a specific side
   *
   * @return Angular velocity in meters per second
   */
  override def angularVelocity = _angularVelocity

  def angularVelocity_=(newVel: Double) = _angularVelocity = newVel

  /**
   * Gets the torque of the mechanical device from a specific side
   *
   * @return force
   */
  override def torque = _torque

  /**
   * Buffer values used by the grid to transfer mechanical energy.
   */
  protected[grid] var bufferTorque = 0D

  /**
   * Angle calculations
   */
  protected var prevTime = 0L
  var prevAngle = 0D

  /**
   * The amount of angle in radians displaced. This is used to align the gear teeth.
   */
  var angleDisplacement = 0D

  /**
   * Events
   */
  @BeanProperty
  var onTorqueChanged: () => Unit = () => ()
  @BeanProperty
  var onVelocityChanged: () => Unit = () => ()

  /**
   * An arbitrary angle value computed based on velocity
   * @return The angle in radians
   */
  def angle: Double =
  {
    val deltaTime = (System.currentTimeMillis() - prevTime) / 1000D
    prevTime = System.currentTimeMillis()
    prevAngle = (prevAngle + deltaTime * angularVelocity) % (2 * Math.PI)
    return prevAngle
  }

  override def rotate(torque: Double)
  {
    bufferTorque += torque
  }

  def power: Double = torque * angularVelocity

  def getMechanicalGrid: MechanicalGrid = super.grid.asInstanceOf[MechanicalGrid]

  override def newGrid: GridNode[NodeMechanical] = new MechanicalGrid

  override def isValidConnection(other: AnyRef): Boolean = other.isInstanceOf[NodeMechanical]
}