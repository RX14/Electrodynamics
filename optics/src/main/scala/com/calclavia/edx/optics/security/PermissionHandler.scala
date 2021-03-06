package com.calclavia.edx.optics.security

import com.calclavia.edx.optics.GraphFrequency
import com.calclavia.edx.optics.api.card.CoordLink
import com.calclavia.edx.optics.component.BlockFrequency
import com.resonant.core.access.Permission
import nova.core.block.Block.RightClickEvent
import nova.core.entity.component.Player
import nova.scala.wrapper.FunctionalWrapper._

/**
 * @author Calclavia
 */
trait PermissionHandler extends BlockFrequency {

	events.add((evt: RightClickEvent) => onRightClick(evt), classOf[RightClickEvent])

	final def hasPermission(playerID: String, permissions: Permission*): Boolean = permissions.forall(hasPermission(playerID, _))

	/**
	 * Gets the first linked biometric identifier, based on the card slots and frequency.
	 */
	def getBiometricIdentifier: BlockBiometric = if (getBiometricIdentifiers.size > 0) getBiometricIdentifiers.head else null

	def getBiometricIdentifiers: Set[BlockBiometric] = {
		val cardLinks = getConnectionCards.view
			.filter(item => item != null && item.isInstanceOf[CoordLink])
			.map(item => item.asInstanceOf[CoordLink].getLink())
			.map(link => link._1.getBlock(link._2).get())
			.collect { case b: BlockBiometric => b }
			.force
			.toSet

		val frequencyLinks = GraphFrequency.instance.get(getFrequency).collect { case b: BlockBiometric => b }

		return frequencyLinks ++ cardLinks
	}

	override def onRightClick(evt: RightClickEvent) {
		val opPlayer = evt.entity.getOp(classOf[Player])
		if (opPlayer.isPresent) {
			if (!hasPermission(opPlayer.get().getPlayerID, MFFSPermissions.configure)) {
				//TODO: Add chat
				//player.addChatMessage(new ChatComponentText("[" + Reference.name + "]" + " Access denied!"))
				evt.result = false
			}
		}
	}

	def hasPermission(playerID: String, permission: Permission): Boolean = !isActive || getBiometricIdentifiers.forall(_.hasPermission(playerID, permission))
}
