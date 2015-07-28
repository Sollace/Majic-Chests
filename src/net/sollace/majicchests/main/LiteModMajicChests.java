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
import com.blazeloader.util.version.BuildType;
import com.blazeloader.util.version.type.ModVersion;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.sollace.majicchests.block.BlockMagicChest;
import net.sollace.majicchests.item.ItemKey;
import net.sollace.majicchests.tileentity.TileEntityMagicChest;
import net.sollace.majicchests.world.MagicChestCollection;

public class LiteModMajicChests implements BLMod, StartupListener, WorldListener {
	public static final String ID = "com.blazeloader.example.majicchests";
	public static final String NAME = "Majic Chests";
	public static final String MOD_DOMAIN = "majicchests";
	public static final String DESCRIPTION = "";
	public final ModVersion VERSION = new ModVersion(BuildType.STABLE, this, 1, 0); 
	
	@Override
	public void init(File configPath) {
		
	}
	
	@Override
	public void start() {
		ApiEntity.registerTileEntity(TileEntityMagicChest.class, "Magic Chest");
		MyBlocks.chest = ApiBlock.quickRegisterBlock(2000, MOD_DOMAIN, "majic_chest", new BlockMagicChest());
		MyItems.key = ApiItem.quickRegisterItem(2001, MOD_DOMAIN, "majic_key", new ItemKey());
		
		if (ApiGeneral.isClient()) {
			ApiRenderItem.registerItem(MyItems.key, MOD_DOMAIN + ":majic_key");
			ApiRenderBlock.registerBuiltInBlocks(MyBlocks.chest);
			ApiRenderBlock.registerFallbackTexture(MyBlocks.chest, "minecraft:blocks/planks_oak");
			ApiRenderBlock.registerBlock(MyBlocks.chest, MOD_DOMAIN + ":majic_chest");
		}
		
		ApiRecipe.addShapedCraftingRecipe(new ItemStack(MyItems.key, 1), " ##", " ##", "  $", '#', Blocks.planks, '$', Items.stick);
		ApiRecipe.addShapedCraftingRecipe(new ItemStack(MyBlocks.chest, 1), "###", "#@#", "###", '@', Blocks.chest, '#', Items.ender_pearl);
	}
	
	@Override
	public void stop() {
		
	}

	@Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath) {
		
	}

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
	
	public void onBlocksAndAmbianceTicked(WorldServer server) {}
	
	public void onServerTick(WorldServer world) {}
	
	public void onWorldInit(WorldServer world) {
		MagicChestCollection.initWorld(world);
	}
	
	public static class MyItems {
		public static ItemKey key;
	}
	
	public static class MyBlocks {
		public static BlockMagicChest chest;
	}
}
