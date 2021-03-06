/*
 * Copyright � 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import net.minecraft.block.material.Material;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import tk.wurst_client.events.listeners.LeftClickListener;
import tk.wurst_client.mods.Mod.Bypasses;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.navigator.settings.ModeSetting;

@Info(category = Category.COMBAT,
	description = "Changes all your hits to critical hits.",
	name = "Criticals",
	tags = "Crits",
	help = "Mods/Criticals")
@Bypasses(ghostMode = false)
public class CriticalsMod extends Mod implements LeftClickListener
{
	private int mode = 1;
	private String[] modes = new String[]{"Jump", "Packet"};
	
	@Override
	public void initSettings()
	{
		settings.add(new ModeSetting("Mode", modes, mode)
		{
			@Override
			public void update()
			{
				mode = getSelected();
			}
		});
	}
	
	@Override
	public NavigatorItem[] getSeeAlso()
	{
		return new NavigatorItem[]{wurst.mods.killauraMod,
			wurst.mods.triggerBotMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(LeftClickListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(LeftClickListener.class, this);
	}
	
	@Override
	public void onLeftClick()
	{
		if(mc.objectMouseOver != null
			&& mc.objectMouseOver.entityHit instanceof EntityLivingBase)
			doCritical();
	}
	
	public void doCritical()
	{
		if(!wurst.mods.criticalsMod.isActive())
			return;
		if(!mc.thePlayer.isInWater()
			&& !mc.thePlayer.isInsideOfMaterial(Material.LAVA)
			&& mc.thePlayer.onGround)
		{
			switch(mode)
			{
				case 0:
					mc.thePlayer.motionY = 0.1F;
					mc.thePlayer.fallDistance = 0.1F;
					mc.thePlayer.onGround = false;
					break;
				case 1:
					double posX = mc.thePlayer.posX;
					double posY = mc.thePlayer.posY;
					double posZ = mc.thePlayer.posZ;
					NetHandlerPlayClient connection = mc.thePlayer.connection;
					
					connection.sendPacket(new CPacketPlayer.Position(posX,
						posY + 0.0625D, posZ, true));
					connection.sendPacket(new CPacketPlayer.Position(posX,
						posY, posZ, false));
					connection.sendPacket(new CPacketPlayer.Position(posX,
						posY + 1.1E-5D, posZ, false));
					connection.sendPacket(new CPacketPlayer.Position(posX,
						posY, posZ, false));
					break;
			}
			
		}
	}
}
