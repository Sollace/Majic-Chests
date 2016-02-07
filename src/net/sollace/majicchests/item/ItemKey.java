package net.sollace.majicchests.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * An item for the key. Pretty simple, just setting its creative tab in the contructor.
 */
public class ItemKey extends Item {
	public ItemKey() {
		super();
		setCreativeTab(CreativeTabs.tabMisc);
		//setFull3D();
	}
}