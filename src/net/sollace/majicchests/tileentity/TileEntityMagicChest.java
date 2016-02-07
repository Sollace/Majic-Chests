package net.sollace.majicchests.tileentity;

import com.blazeloader.api.entity.ApiEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.LockCode;
import net.sollace.majicchests.world.InventoryMagicChest;

public class TileEntityMagicChest extends TileEntityChest {
	
	private boolean primary = true;
	
	public TileEntityMagicChest() {
		super();
	}
	
	/**
	 * Returns true if this chest represents the upper half of a double chest.
	 */
	private boolean isPrimary() {
		return primary;
	}
	
	/**
	 * Sets this chest as the upper part of a double chest.
	 * @param prime
	 */
	public void setPrimary(boolean prime) {
		primary = prime;
	}
	
	public boolean hasCustomName() {
		return true;
	}
	
	public String getCommandSenderName() {
		return "Majic Chest: " + getInventoryIdentifier();
	}
	
	/**
	 * Gets the inventory contents this chest is associated with.
	 */
	public InventoryMagicChest getInventory() {
		return InventoryMagicChest.getInventory(worldObj, getInventoryIdentifier());
	}
	
	/**
	 * Gets the identifier used to look up contents for this chest
	 */
	public String getInventoryIdentifier() {
		return getLockCode().getLock();
	}
	
	/**
	 * Checks if this chest has an associated to access an inventory.
	 */
	public boolean hasKey() {
		return super.isLocked();
	}
	
	public boolean isLocked() {
		return !hasKey();
	}
	
	/**
	 * Associates this chest with the provided key
	 */
	public void unlock(ItemStack keyIn) {
		setPrimary(true);
		setLockCode(new LockCode(keyIn.getDisplayName()));
	}
	
	/**
	 * Disassociates this chest with any contents.
	 */
	public void lock() {
		setLockCode(null);
	}
	
	public IChatComponent getDisplayName() {
		return new ChatComponentText("Majic Chest: " + getInventoryIdentifier());
	}
	
	public int getSizeInventory() {
		return getInventory().getSizeInventory();
	}
	
	public int getInventoryStackLimit() {
		return getInventory().getInventoryStackLimit();
	}
	
	public ItemStack getStackInSlot(int index) {
		return getInventory().getStackInSlot(isPrimary(), index);
	}
	
	public ItemStack getStackInSlotOnClosing(int index) {
		return getInventory().getStackInSlot(isPrimary(), index);
	}
	
	public ItemStack decrStackSize(int index, int count) {
		return getInventory().decrStackSize(isPrimary(), index, count);
	}
	
	public void setInventorySlotContents(int index, ItemStack stack) {
		getInventory().setInventorySlotContents(isPrimary(), index, stack);
	}
	
	public boolean isUseableByPlayer(EntityPlayer player) {
		return super.isUseableByPlayer(player);
	}
	
	public void clear() {
		getInventory().clear();
	}
	
	public void readFromNBT(NBTTagCompound compound) {
		pos = new BlockPos(compound.getInteger("x"), compound.getInteger("y"), compound.getInteger("z"));
		setLockCode(LockCode.fromNBT(compound));
	}
	
	public void writeToNBT(NBTTagCompound compound) {
		String id = ApiEntity.getTileEntityID(this);
        if (id == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        } else {
            compound.setString("id", id);
            compound.setInteger("x", pos.getX());
            compound.setInteger("y", pos.getY());
            compound.setInteger("z", pos.getZ());
        }
		if (getLockCode() != null) {
            getLockCode().toNBT(compound);
        }
	}
	
	public void update() {
		super.update();
	}
}
