package com.aguga.Utils;

import com.aguga.ChestLogs;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.*;
import net.minecraft.block.enums.ChestType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        if (!(blockEntity instanceof ChestBlockEntity)) return null;
        Direction facingDirection = blockState.get(Properties.HORIZONTAL_FACING);

        ChestType chestType = blockState.get(ChestBlock.CHEST_TYPE);
        switch (chestType)
        {
            case LEFT:
                if (facingDirection == Direction.EAST) return (ChestBlockEntity) world.getBlockEntity(blockEntity.getPos().south());
                else if (facingDirection == Direction.SOUTH) return (ChestBlockEntity) world.getBlockEntity(blockEntity.getPos().west());
                else if (facingDirection == Direction.WEST) return (ChestBlockEntity) world.getBlockEntity(blockEntity.getPos().north());
                else return (ChestBlockEntity) world.getBlockEntity(blockEntity.getPos().east());
            case RIGHT:
                if (facingDirection == Direction.EAST) return (ChestBlockEntity) world.getBlockEntity(blockEntity.getPos().north());
                else if (facingDirection == Direction.SOUTH) return (ChestBlockEntity) world.getBlockEntity(blockEntity.getPos().east());
                else if (facingDirection == Direction.WEST) return (ChestBlockEntity) world.getBlockEntity(blockEntity.getPos().south());
                else return (ChestBlockEntity) world.getBlockEntity(blockEntity.getPos().west());
            default:
                return null;
        }
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

            FileWriter fileWriter = new FileWriter(path, true);
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
        String[] itemsList = itemsStr.substring(1, itemsStr.length() - 1).split(",");
        List<String> outputList = new ArrayList<>();

        for (String item : itemsList)
        {
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
        Map<String, Integer> countMap1 = new HashMap<>();
        Map<String, Integer> countMap2 = new HashMap<>();

        // Count occurrences in list1
        for (String item : list1)
        {
            String[] parts = item.split(" ", 2);
            int count = Integer.parseInt(parts[0]);
            String name = parts[1];
            countMap1.put(name, countMap1.getOrDefault(name, 0) + count);
        }

        // Count occurrences in list2
        for (String item : list2)
        {
            String[] parts = item.split(" ", 2);
            int count = Integer.parseInt(parts[0]);
            String name = parts[1];
            countMap2.put(name, countMap2.getOrDefault(name, 0) + count);
        }

        List<String> outputList = new ArrayList<>();

        // Calculate the differences
        for (Map.Entry<String, Integer> entry : countMap2.entrySet())
        {
            String name = entry.getKey();
            int count2 = entry.getValue();
            int count1 = countMap1.getOrDefault(name, 0);
            int countDiff = count2 - count1;
            if (countDiff > 0) {
                outputList.add(countDiff + " " + name);
            }
        }

        return outputList;
    }
}
