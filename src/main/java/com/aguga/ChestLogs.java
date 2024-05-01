package com.aguga;

import com.aguga.Utils.ChestClosedCallback;
import com.aguga.Utils.Utils;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class ChestLogs implements ModInitializer
{
	public static String chestClosedBy = "";
    public static final Logger LOGGER = LoggerFactory.getLogger("chest-log");
	List<String> folderPaths = new ArrayList<>();

	@Override
	public void onInitialize()
	{
		ServerLifecycleEvents.SERVER_STARTED.register((server ->
		{
			folderPaths.add(server.getSavePath(WorldSavePath.ROOT).resolve("ChestLog").toString());
			folderPaths.add(server.getSavePath(WorldSavePath.ROOT).resolve("ChestLog" + File.separator + "Overworld").toString());
			folderPaths.add(server.getSavePath(WorldSavePath.ROOT).resolve("ChestLog" + File.separator + "The Nether").toString());
			folderPaths.add(server.getSavePath(WorldSavePath.ROOT).resolve("ChestLog" + File.separator + "The End").toString());

			for(String folderPath : folderPaths)
			{
				File folder = new File(folderPath);

				if (!folder.exists()) {
					boolean created = folder.mkdirs();
					if (!created)
						LOGGER.error("Failed to create folder: " + folderPath);
				}
			}
		}));

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) ->
		{
			chestClosedBy = "";
			BlockPos blockPos = hitResult.getBlockPos();
			BlockState blockState = world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			ChestBlockEntity secondChest = Utils.getSecondChest(blockState, blockEntity, world);

			List<Block> chestBlocks = Arrays.asList(Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.BARREL, Blocks.SHULKER_BOX);
			if(!chestBlocks.contains(block))
				return ActionResult.PASS;

			String blockstr = block.getName().getString();

			AtomicReference<String> timeStamp = new AtomicReference<>(Utils.getTimeStamp());
			String dimStr = Utils.getDimString(world.getDimensionEntry());

			String savePath = folderPaths.get(0) + File.separator + dimStr + File.separator + blockstr + " " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ();

			List<ItemStack> itemsBefore = Utils.getItems((LootableContainerBlockEntity) blockEntity);
			if(secondChest != null)
				itemsBefore.addAll(Utils.getItems(secondChest));
			List<String> itemsBeforeStr = Utils.itemStackListToStrList(itemsBefore);

			CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
			{
				while(!chestClosedBy.equals(player.getName().toString()))
				{

				}

				List<ItemStack> itemsAfter = Utils.getItems((LootableContainerBlockEntity) blockEntity);
				if(secondChest != null)
					itemsAfter.addAll(Utils.getItems(secondChest));
				List<String> itemsAfterStr = Utils.itemStackListToStrList(itemsAfter);
				timeStamp.set(Utils.getTimeStamp());

				List<String> itemsAdded = Utils.getChangedItems(itemsBeforeStr, itemsAfterStr);
				List<String> itemsRemoved = Utils.getChangedItems(itemsAfterStr, itemsBeforeStr);

				//LOGGER.info("Items Added: " + itemsAdded);
				//LOGGER.info("Items Removed: " + itemsRemoved);

				Utils.writeLog(timeStamp.get(), player.getDisplayName().getString(), savePath, itemsAdded, itemsRemoved);
			});
			return ActionResult.PASS;
		});

		ChestClosedCallback.EVENT.register((player ->
		{
			chestClosedBy = player.getName().toString();
		}));
	}
}