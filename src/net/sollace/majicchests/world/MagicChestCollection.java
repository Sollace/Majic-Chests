package net.sollace.majicchests.world;

import java.util.HashMap;
import java.util.Map;

import com.blazeloader.api.world.ApiWorld;
import com.blazeloader.api.world.gen.WorldSavedDataCollection;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;

/**
 * World storage for magic chests.
 */
public class MagicChestCollection extends WorldSavedDataCollection {
	
	private static Map<World, MagicChestCollection> collections = new HashMap<World, MagicChestCollection>();
	
	private Map<String, InventoryMagicChest> inventoryRegistry = new HashMap<String, InventoryMagicChest>();
	
	/**
	 * Called when a world is created/loaded.
	 * Will register a collection to the world's storage system and keep a mapping to retrieve later.
	 * 
	 * @param world	The current world
	 */
	public static void initWorld(WorldServer world) {
		collections.put(world, ApiWorld.registerWorldData(world, MagicChestCollection.class, MagicChestCollection.fileNameForProvider(world.provider)));
	}
	
	/**
	 * Gets a magic chest collection associated with the given world
	 * @param w	The current world
	 */
	public static MagicChestCollection getCollection(World w) {
		if (collections.containsKey(w)) {
			return collections.get(w);
		}
		return null;
	}
	
	public MagicChestCollection(World w) {
        this(fileNameForProvider(w.provider));
        markDirty();
    }
	
	public MagicChestCollection(String name) {
		super(name);
	}
	
	public InventoryMagicChest getInventory(String identifier) {
		if (!inventoryRegistry.containsKey(identifier)) {
			inventoryRegistry.put(identifier, new InventoryMagicChest(this, identifier));
		}
		return inventoryRegistry.get(identifier);
	}
	
	public static String fileNameForProvider(WorldProvider provider) {
        return "magicchests" + provider.getInternalNameSuffix();
    }
	
	public void readFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("chests")) {
			NBTTagList list = nbt.getTagList("chests", 10);
			for (int i = 0; i < list.tagCount(); i++) {
	            InventoryMagicChest inv = InventoryMagicChest.fromNBT(this, list.getCompoundTagAt(i));
	            inventoryRegistry.put(inv.getName(), inv);
	        }
		}
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagList list = new NBTTagList();
		for (InventoryMagicChest i : inventoryRegistry.values()) {
			if (!i.isEmpty()) {
	            NBTTagCompound tag = new NBTTagCompound();
	            i.writeToNBT(tag);
	            list.appendTag(tag);
			}
		}
		if (list.tagCount() > 0) {
			nbt.setTag("chests", list);
		}
	}
	
	public void setWorldsForAll(World worldIn) {
		
	}
}
