package net.sollace.majicchests.block;

import java.util.Iterator;

import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.sollace.majicchests.item.ItemKey;
import net.sollace.majicchests.tileentity.TileEntityMagicChest;

/**
 * The block for a magic chest. Extends from BlockChest and overrides functionality as needed.
 * 
 */
public class BlockMagicChest extends BlockChest {

	public BlockMagicChest() {
		super(2);
		setHardness(2.5F);
		setStepSound(soundTypeWood);
	}
	
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        worldIn.updateComparatorOutputLevel(pos, this);
        worldIn.removeTileEntity(pos);
    }
	
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (hasAKey(playerIn)) {
			unlock(worldIn, pos, playerIn);
		} else {
			lock(worldIn, pos);
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
	}
	
	public ILockableContainer getLockableContainer(World worldIn, BlockPos pos) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        
        if (!(tileEntity instanceof TileEntityMagicChest)) return null;
        
        TileEntityMagicChest myEntity = (TileEntityMagicChest)tileEntity;

        if (isBlocked(worldIn, pos)) return null;
        
        Iterator<EnumFacing> iter = EnumFacing.Plane.HORIZONTAL.iterator();
        while (iter.hasNext()) {
            EnumFacing i = iter.next();
            BlockPos other = pos.offset(i);
            
            if (worldIn.getBlockState(other).getBlock() == this) {
                if (isBlocked(worldIn, other)) return null;

                tileEntity = worldIn.getTileEntity(other);
                if (tileEntity instanceof TileEntityMagicChest) {
                	TileEntityMagicChest hisEntity = (TileEntityMagicChest)tileEntity;
                	ILockableContainer result;
                    if (i != EnumFacing.WEST && i != EnumFacing.NORTH) {
                    	myEntity.setPrimary(true);
                    	hisEntity.setLockCode(myEntity.getLockCode());
                    	hisEntity.setPrimary(false);
                        return new InventoryLargeChest("container.chestDouble", myEntity, hisEntity);
                    } else {
                    	myEntity.setPrimary(false);
                    	hisEntity.setLockCode(myEntity.getLockCode());
                    	hisEntity.setPrimary(true);
                        return new InventoryLargeChest("container.chestDouble", hisEntity, myEntity);
                    }
                }
            }
        }
        return (ILockableContainer)myEntity;
    }
	
	public boolean isBlocked(World worldIn, BlockPos pos) {
        return isBelowSolidBlock(worldIn, pos) || isOcelotSittingOnChest(worldIn, pos);
    }

    public boolean isBelowSolidBlock(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.up()).getBlock().isNormalCube();
    }

    public boolean isOcelotSittingOnChest(World worldIn, BlockPos pos) {
        Iterator var3 = worldIn.getEntitiesWithinAABB(EntityOcelot.class, new AxisAlignedBB((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 2), (double)(pos.getZ() + 1))).iterator();
        EntityOcelot e;
        do {
            if (!var3.hasNext()) return false;
            Entity var4 = (Entity)var3.next();
            e = (EntityOcelot)var4;
        } while (!e.isSitting());
        return true;
    }
	
    /**
     * Checks if the player has an item that may serve as a key for this chest
     */
	public boolean hasAKey(EntityPlayer player) {
		ItemStack stack = player.getCurrentEquippedItem();
		return stack != null && stack.hasDisplayName() && stack.getItem() instanceof ItemKey;
	}
	
	/**
	 * Attempts to associate this chest with any key held by the player
	 * @param w			The current world
	 * @param pos		Current block position
	 * @param player	The player attempting to access this chest
	 */
	public void unlock(World w, BlockPos pos, EntityPlayer player) {
		((TileEntityMagicChest)w.getTileEntity(pos)).unlock(player.getCurrentEquippedItem());
	}
	
	/**
	 * Locks this chest
	 */
	public void lock(World w, BlockPos pos) {
		((TileEntityMagicChest)w.getTileEntity(pos)).lock();
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityMagicChest();
	}
}
