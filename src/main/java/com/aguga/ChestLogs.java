package com.aguga;

import com.aguga.Utils.Utils;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class ChestLogs implements ModInitializer
{
    public static final Logger LOGGER = LoggerFactory.getLogger("chest-log");
	String folderPath;

	@Override
	public void onInitialize()
	{
		ServerLifecycleEvents.SERVER_STARTED.register((server ->
		{
			folderPath = server.getSavePath(WorldSavePath.ROOT).resolve("ChestLog").toString();
			File folder = new File(folderPath);

			if (!folder.exists())
			{
				boolean created = folder.mkdirs();
				if (!created)
					LOGGER.error("Failed to create folder: " + folderPath);
			}
		}));

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) ->
		{
			Block block = world.getBlockState(hitResult.getBlockPos()).getBlock();
			BlockEntity blockEntity = world.getBlockEntity(hitResult.getBlockPos());

			List<Block> chestBlocks = Arrays.asList(Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.BARREL, Blocks.SHULKER_BOX);
			if(!chestBlocks.contains(block))
				return ActionResult.PASS;

			String blockstr = block.getName().getString();
			BlockPos pos = hitResult.getBlockPos();

			AtomicReference<String> timeStamp = new AtomicReference<>(Utils.getTimeStamp());
			String dimStr = Utils.getDimString(world.getDimensionKey());

			String savePath = folderPath + File.separator + blockstr + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " " + dimStr;

			List<ItemStack> itemsBefore = List.copyOf(Utils.getItems(blockEntity));
			List<String> itemsBeforeStr = Utils.itemStackListToStrList(itemsBefore);

			CompletableFuture<Void> chestClosed = CompletableFuture.runAsync(() ->
			{
				try
				{
					Thread.sleep(2000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}

				Vec3d initialPos = player.getPos();
				Float initialYaw = player.getYaw();
				Float initialPitch = player.getPitch();

				Float currentYaw = player.getYaw();
				Float currentPitch = player.getPitch();
				Vec3d currentPos = player.getPos();

				while(initialYaw.equals(currentYaw) && initialPitch.equals(currentPitch) && initialPos.equals(currentPos))
				{
					try
					{
						Thread.sleep(400);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					currentPos = player.getPos();
					currentYaw = player.getYaw();
					currentPitch = player.getPitch();
				}

				List<ItemStack> itemsAfter = Utils.getItems(blockEntity);
				List<String> itemsAfterStr = Utils.itemStackListToStrList(itemsAfter);
				timeStamp.set(Utils.getTimeStamp());

				List<String> itemsAdded = Utils.getChangedItems(itemsBeforeStr, itemsAfterStr);
				List<String> itemsRemoved = Utils.getChangedItems(itemsAfterStr, itemsBeforeStr);

				Utils.writeLog(timeStamp.get(), player.getDisplayName().getString(), savePath, itemsAdded, itemsRemoved);
			});
			return ActionResult.PASS;
		});
	}
}