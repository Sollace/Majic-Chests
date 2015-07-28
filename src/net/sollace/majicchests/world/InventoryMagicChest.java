package net.sollace.majicchests.world;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class InventoryMagicChest {
	
	
	public static InventoryMagicChest getInventory(World w, String identifier) {
		return MagicChestCollection.getCollection(w).getInventory(identifier);
	}
	
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
	
	public String getCommandSenderName() {return null;}
	
	public int getInventoryStackLimit() {return 64;}
	
	public String getName() {
		return inventoryName;
	}
	
	public int getSizeInventory() {return 27;}
	
	public ItemStack getStackInSlot(boolean primary, int index) {
		return getContents(primary)[index];
	}
	
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
	
	public ItemStack getStackInSlotOnClosing(boolean primary, int index) {
		ItemStack[] contents = getContents(primary);
		if (contents[index] != null) {
            ItemStack result = contents[index];
            contents[index] = null;
            return result;
        }
        return null;
	}
	
	public ItemStack[] getContents(boolean primary) {
		return primary ? top : bottom;
	}
	
	public void setInventorySlotContents(boolean primary, int index, ItemStack stack) {
		ItemStack[] contents = getContents(primary);
		contents[index] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
        parent.markDirty();
	}
	
	public void clear() {
		for (int i = 0; i < top.length; ++i) {
            top[i] = null;
        }
		for (int i = 0; i < bottom.length; ++i) {
            bottom[i] = null;
        }
	}
	
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
