package com.aguga.Utils;

import com.aguga.ChestLogs;
import net.minecraft.block.entity.*;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.awt.*;
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
        if(dimensionTypeRegistryEntry.getKey().equals(DimensionTypes.OVERWORLD))
            dimStr = "Overworld";
        else if(dimensionTypeRegistryEntry.getKey().equals(DimensionTypes.THE_NETHER))
            dimStr = "Nether";
        else if(dimensionTypeRegistryEntry.getKey().equals(DimensionTypes.THE_END))
            dimStr = "End";
        return dimStr;
    }

    public static List<ItemStack> getItems(BlockEntity blockEntity)
    {
        List<ItemStack> itemStacks = new ArrayList<>();

        if(blockEntity instanceof ChestBlockEntity)
        {
            ChestBlockEntity chestEntity = (ChestBlockEntity) blockEntity;
            for(int i = 0; i < chestEntity.size(); i++)
            {
                ItemStack itemStack = chestEntity.getStack(i);
                if(!itemStack.isEmpty())
                {
                    itemStacks.add(itemStack);
                }
            }
        }

        if(blockEntity instanceof BarrelBlockEntity)
        {
            BarrelBlockEntity barrelEntity = (BarrelBlockEntity) blockEntity;
            for(int i = 0; i < barrelEntity.size(); i++)
            {
                ItemStack itemStack = barrelEntity.getStack(i);
                if(!itemStack.isEmpty())
                {
                    itemStacks.add(itemStack);
                }
            }
        }

        if(blockEntity instanceof ShulkerBoxBlockEntity)
        {
            ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity) blockEntity;
            for(int i = 0; i < shulkerBoxBlockEntity.size(); i++)
            {
                ItemStack itemStack = shulkerBoxBlockEntity.getStack(i);
                if(!itemStack.isEmpty())
                {
                    itemStacks.add(itemStack);
                }
            }
        }

        if(blockEntity instanceof TrappedChestBlockEntity)
        {
            TrappedChestBlockEntity trappedChestBlockEntity = (TrappedChestBlockEntity) blockEntity;
            for(int i = 0; i < trappedChestBlockEntity.size(); i++)
            {
                ItemStack itemStack = trappedChestBlockEntity.getStack(i);
                if(!itemStack.isEmpty())
                {
                    itemStacks.add(itemStack);
                }
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
