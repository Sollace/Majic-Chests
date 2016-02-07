package net.sollace.majicchests.world;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

/**
 * Inventory for a Magic Chest. Implements each half as a separate set of slots.
 */
public class InventoryMagicChest {
	
	/**
	 * Gets the inventory from word storage corresponding to the identifier provided
	 * 
	 * @param w				The current world
	 * @param identifier	Name of inventory
	 * @return	A matching inventory object
	 */
	public static InventoryMagicChest getInventory(World w, String identifier) {
		return MagicChestCollection.getCollection(w).getInventory(identifier);
	}
	
	/**
	 * Loads an inventory from nbt data
	 * @param collection	The collection this chest belongs to
	 * @param compound		nbt data for the inventory
	 */
	public static InventoryMagicChest fromNBT(MagicChestCollection collection, NBTTagCompound compound) {
		return new InventoryMagicChest(collection, compound);
	}
	
	private final MagicChestCollection parent;
	
	private String inventoryName;
	
	private ItemStack[] top;
	private ItemStack[] bottom;
	
	private InventoryMagicChest(MagicChestCollection collection, NBTTagCompound compound) {
		readFromNBT(compound);
		parent = collection;
	}
	
	public InventoryMagicChest(MagicChestCollection collection, String name) {
		inventoryName = name;
		top = new ItemStack[27];
		bottom = new ItemStack[27];
		parent = collection;
	}
	
	/**
	 * Gets the maximum stack size slots in this inventory can take.
	 */
	public int getInventoryStackLimit() {
		return 64;
	}
	
	/**
	 * Gets the name of this inventory for display
	 */
	public String getName() {
		return inventoryName;
	}
	
	/**
	 * Gets the number of slots available in this inventory
	 */
	public int getSizeInventory() {
		return 27;
	}
	
	/**
	 * Gets the stack from the indicated slot index.
	 * 
	 * @param primary	True if we are accessing the top half of the chest
	 * @param index		Index of the slot
	 * @return	The stack in the slot, or null
	 */
	public ItemStack getStackInSlot(boolean primary, int index) {
		return getContents(primary)[index];
	}
	
	/**
	 * Reduces the size of the stack in the chosen slot by the specified amount
	 * 
	 * @param primary	True if we are accessing the top half of this chest
	 * @param index		Index of the slot to access
	 * @param count		Number of items to subtract
	 * @return	The items removed from the slot
	 */
	public ItemStack decrStackSize(boolean primary, int index, int count) {
		ItemStack stack = null;
		ItemStack[] contents = getContents(primary);
		if (contents[index] != null) {
            if (contents[index].stackSize <= count) {
                stack = contents[index];
                contents[index] = null;
                parent.markDirty();
            } else {
                stack = contents[index].splitStack(count);
                if (contents[index].stackSize == 0) {
                    contents[index] = null;
                }
                parent.markDirty();
            }
        }
        return stack;
	}
	
	/**
	 * Removes the contents of a slot.
	 * 
	 * @param primary	True if we are accessing the top half of this chest
	 * @param index		Index of the slot to access
	 * @return			The contents of the slot, or null.
	 */
	public ItemStack getStackInSlotOnClosing(boolean primary, int index) {
		ItemStack[] contents = getContents(primary);
		if (contents[index] != null) {
            ItemStack result = contents[index];
            contents[index] = null;
            return result;
        }
        return null;
	}
	
	/**
	 * Gets the inventory slots for the specified half of this chest.
	 * 
	 * @param primary	True if we are accessing the top half of this chest
	 * 
	 * @return	An array of slots
	 */
	public ItemStack[] getContents(boolean primary) {
		return primary ? top : bottom;
	}
	
	/**
	 * Sets the contents of a slot.
	 * 
	 * @param primary	True if we are accessing the top half of this chest
	 * @param index		Index of the slot to modify
	 * @param stack		Contents to place into the slot
	 */
	public void setInventorySlotContents(boolean primary, int index, ItemStack stack) {
		ItemStack[] contents = getContents(primary);
		contents[index] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
        parent.markDirty();
	}
	
	/**
	 * Empties this chest.
	 */
	public void clear() {
		for (int i = 0; i < top.length; ++i) {
            top[i] = null;
        }
		for (int i = 0; i < bottom.length; ++i) {
            bottom[i] = null;
        }
	}
	
	/**
	 * Checks if there are any items in this inventory
	 */
	public boolean isEmpty() {
		for (int i = 0; i < top.length; i++) {
			if (top[i] != null && top[i].stackSize > 0) {
				return false;
			}
		}
		for (int i = 0; i < bottom.length; i++) {
			if (bottom[i] != null && bottom[i].stackSize > 0) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Loads this chest's contents from nbt data
	 */
	public void readFromNBT(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("Items_Top", 10);
        inventoryName = compound.getString("CustomName");
        top = new ItemStack[this.getSizeInventory()];
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int slot = tag.getByte("Slot") & 255;
            if (slot >= 0 && slot < top.length) {
                top[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }
        list = compound.getTagList("Items_Bottom", 10);
        bottom = new ItemStack[this.getSizeInventory()];
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int slot = tag.getByte("Slot") & 255;
            if (slot >= 0 && slot < bottom.length) {
                bottom[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }
    }
	
	/**
	 * Saves this chest's contents to nbt data
	 */
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < top.length; i++) {
            if (top[i] != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte)i);
                top[i].writeToNBT(tag);
                list.appendTag(tag);
            }
        }
        compound.setString("CustomName", getName());
        compound.setTag("Items_Top", list);
        list = new NBTTagList();
        for (int i = 0; i < bottom.length; i++) {
            if (bottom[i] != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte)i);
                bottom[i].writeToNBT(tag);
                list.appendTag(tag);
            }
        }
        compound.setTag("Items_Bottom", list);
    }
}
