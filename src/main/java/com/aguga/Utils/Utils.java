package com.aguga.Utils;

import com.aguga.ChestLogs;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.*;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils
{
    public static String getTimeStamp()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yy  HH:mm:ss");
        return String.format("[%s]", dtf.format(LocalDateTime.now()));
    }

    public static String getDimString(RegistryEntry<DimensionType> dimensionTypeRegistryEntry)
    {
        String dimStr = "unkown";
        if(dimensionTypeRegistryEntry.matchesKey(DimensionTypes.OVERWORLD))
            dimStr = "Overworld";
        else if(dimensionTypeRegistryEntry.matchesKey(DimensionTypes.THE_NETHER))
            dimStr = "The Nether";
        else if(dimensionTypeRegistryEntry.matchesKey(DimensionTypes.THE_END))
            dimStr = "The End";
        return dimStr;
    }

    public static ChestBlockEntity getSecondChest(BlockState blockState, BlockEntity blockEntity, World world)
    {
        if(!(blockEntity instanceof ChestBlockEntity))
            return null;

        ChestBlockEntity chestBlockEntity;
        Direction facingDirection = blockState.get(Properties.HORIZONTAL_FACING);

        if(blockState.get(ChestBlock.CHEST_TYPE) == ChestType.LEFT)
        {
            if(facingDirection.equals(Direction.EAST))
                chestBlockEntity = (ChestBlockEntity) world.getBlockEntity(blockEntity.getPos().south());
            else if(facingDirection.equals(Direction.SOUTH))
                chestBlockEntity = (ChestBlockEntity) world.getBlockEntity(blockEntity.getPos().west());
            else if(facingDirection.equals(Direction.WEST))
                chestBlockEntity = (ChestBlockEntity) world.getBlockEntity(blockEntity.getPos().north());
            else
                chestBlockEntity = (ChestBlockEntity) world.getBlockEntity(blockEntity.getPos().east());
        } else if(blockState.get(ChestBlock.CHEST_TYPE) == ChestType.RIGHT)
        {
            if(facingDirection.equals(Direction.EAST))
                chestBlockEntity = (ChestBlockEntity) world.getBlockEntity(blockEntity.getPos().north());
            else if(facingDirection.equals(Direction.SOUTH))
                chestBlockEntity = (ChestBlockEntity) world.getBlockEntity(blockEntity.getPos().east());
            else if(facingDirection.equals(Direction.WEST))
                chestBlockEntity = (ChestBlockEntity) world.getBlockEntity(blockEntity.getPos().south());
            else
                chestBlockEntity = (ChestBlockEntity) world.getBlockEntity(blockEntity.getPos().west());
        } else
        {
            chestBlockEntity = null;
        }
        return chestBlockEntity;
    }

    public static List<ItemStack> getItems(LootableContainerBlockEntity blockEntity)
    {
        List<ItemStack> itemStacks = new ArrayList<>();

        for(int i = 0; i < blockEntity.size(); i++)
        {
            ItemStack itemStack = blockEntity.getStack(i);
            if(!itemStack.isEmpty())
            {
                itemStacks.add(itemStack);
            }
        }
        return itemStacks;
    }

    public static void writeLog(String timeStamp, String playerName, String path, List<String> itemsAdded, List<String> itemsRemoved)
    {
        try
        {
            String logStr;

            logStr = timeStamp + " " + playerName + "\nItems Added: " + itemsAdded + "\nItems Removed: " + itemsRemoved + "\n\n";

            FileWriter fileWriter = new FileWriter(new File(path), true);
            fileWriter.write(logStr);
            fileWriter.close();
        } catch (IOException e)
        {
            ChestLogs.LOGGER.error("Error writing log");
            e.printStackTrace();
        }
    }

    public static List<String> itemStackListToStrList(List<ItemStack> items)
    {
        String itemsStr = items.toString();
        List<String> itemsList = Arrays.asList(itemsStr.substring(1, itemsStr.length() - 1).split(","));
        List<String> outputList = new ArrayList<>();

        for (String item : itemsList) {
            if(item.isEmpty()) continue;

            String[] parts = item.trim().split(" ");
            if(parts.length < 2) continue;

            String countStr = parts[0];
            String type = parts[1];

            outputList.add(countStr + " " + type);
        }
        return outputList;
    }

    public static List<String> getChangedItems(List<String> list1, List<String> list2)
    {
        List<String> outputList = new ArrayList<>();
        List<String> done = new ArrayList<>();

        Integer count;
        Integer count1;
        Integer count2;

        for(String item : list2)
        {
            if(done.contains(item.split(" ", 2)[1]))
                continue;

            count = 0;
            count1 = 0;
            count2 = 0;
            for(String str : list2)
            {
                if(str.split(" ", 2)[1].equals(item.split(" ", 2)[1]))
                {
                    count1 += Integer.parseInt(str.split(" ")[0]);
                }
            }

            for(String str : list1)
            {
                if(str.split(" ", 2)[1].equals(item.split(" ", 2)[1]))
                {
                    count2 += Integer.parseInt(str.split(" ")[0]);
                }
            }

            count = count1 - count2;
            if(count > 0)
            {
                outputList.add(outputList.size(), count + " " + item.split(" ", 2)[1]);
            }
            done.add(item.split(" ", 2)[1]);
        }
        return outputList;
    }
}
