package net.sollace.majicchests.main;

import java.io.File;

import com.blazeloader.api.ApiGeneral;
import com.blazeloader.api.block.ApiBlock;
import com.blazeloader.api.client.render.ApiRenderBlock;
import com.blazeloader.api.client.render.ApiRenderItem;
import com.blazeloader.api.entity.ApiEntity;
import com.blazeloader.api.item.ApiItem;
import com.blazeloader.api.recipe.ApiRecipe;
import com.blazeloader.bl.mod.BLMod;
import com.blazeloader.event.listeners.StartupListener;
import com.blazeloader.event.listeners.WorldListener;
import com.blazeloader.event.listeners.args.EntitySpawnEventArgs;
import com.blazeloader.util.version.BuildType;
import com.blazeloader.util.version.type.ModVersion;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.sollace.majicchests.block.BlockMagicChest;
import net.sollace.majicchests.item.ItemKey;
import net.sollace.majicchests.tileentity.TileEntityMagicChest;
import net.sollace.majicchests.world.MagicChestCollection;

/**
 * Main class for this mod.
 * 
 */
public class LiteModMajicChests implements BLMod, StartupListener, WorldListener {
	/**
	 * A unique identifier for this mod. This should not be changed after release
	 */
	public static final String ID = "com.blazeloader.example.majicchests";
	/**
	 * A more friendly display name
	 */
	public static final String NAME = "Majic Chests";
	/**
	 * The resourcepack domain for this mod's assets
	 */
	public static final String MOD_DOMAIN = "majicchests";
	/**
	 * A description to say what the mod does.
	 */
	public static final String DESCRIPTION = "";
	
	public final ModVersion VERSION = new ModVersion(BuildType.STABLE, this, 1, 0); 
	
	@Override
	public void init(File configPath) {
		
	}
	
	/**
	 * Called once the game loop has begun. 
	 */
	@Override
	public void start() {
		ApiEntity.registerTileEntity(TileEntityMagicChest.class, "Magic Chest");
		
		//Quickly add the chest block.
		//Don't need any special behaviour here so we can let Blazeloader initialise names, and items for us
		MyBlocks.chest = ApiBlock.quickRegisterBlock(2000, MOD_DOMAIN, "majic_chest", new BlockMagicChest());
		//Register the item in the same way.
		MyItems.key = ApiItem.quickRegisterItem(2001, MOD_DOMAIN, "majic_key", new ItemKey());
		
		if (ApiGeneral.isClient()) {
			//Registering rendering logic.
			//This can only be done on the client.
			
			//Register a name for the key
			ApiRenderItem.registerItem(MyItems.key, MOD_DOMAIN + ":majic_key");
			
			//Register the chest as a built-in block. It's rendering will be handled by the tileentity renderer.
			ApiRenderBlock.registerBuiltInBlocks(MyBlocks.chest);
			//Set the game to use a plank texture for the chest's particles, since it doesn't have a taxture of its own
			ApiRenderBlock.registerFallbackTexture(MyBlocks.chest, "minecraft:blocks/planks_oak");
			//Register the block with a name for rendering as an item
			ApiRenderBlock.registerBlock(MyBlocks.chest, MOD_DOMAIN + ":majic_chest");
		}
		
		// Recipe to craft a key
		//  _##
		//  _##
		//  __$
		ApiRecipe.addShapedCraftingRecipe(new ItemStack(MyItems.key, 1), " ##", " ##", "  $", '#', Blocks.planks, '$', Items.stick);
		// Rcipe to craft a magic chest
		//  ###
		//  #@#
		//  ###
		ApiRecipe.addShapedCraftingRecipe(new ItemStack(MyBlocks.chest, 1), "###", "#@#", "###", '@', Blocks.chest, '#', Items.ender_pearl);
	}
	
	/**
	 * Called when the game shuts down
	 */
	@Override
	public void stop() { }

	@Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath) { }

	@Override
	public String getModId() {
		return ID;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getVersion() {
		return "b1.0";
	}

	@Override
	public String getModDescription() {
		return DESCRIPTION;
	}

	@Override
	public ModVersion getModVersion() {
		return VERSION;
	}
	
	/**
	 * Called every tick by the server to update world states
	 */
	public void onBlocksAndAmbianceTicked(WorldServer server) {}
	
	/**
	 * Called every tick by the server
	 */
	public void onServerTick(WorldServer world) {}
	
	/**
	 * Called when a world is loaded
	 */
	public void onWorldInit(WorldServer world) {
		MagicChestCollection.initWorld(world);
	}
	
	/**
     * Called when an entity is spawned in the world.
     */
	public void onEntitySpawned(World world, EntitySpawnEventArgs args) {}
	
	public static class MyItems {
		public static ItemKey key;
	}
	
	public static class MyBlocks {
		public static BlockMagicChest chest;
	}
}
