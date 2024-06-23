package com.aguga.Utils;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemPlacementContext;

@FunctionalInterface
public interface BlockPlaceCallback
{
    void invoke(ItemPlacementContext context);

    Event<BlockPlaceCallback> EVENT = EventFactory.createArrayBacked(BlockPlaceCallback.class,
            (listeners) -> (context) ->
            {
                for (BlockPlaceCallback listener : listeners)
                {
                    listener.invoke(context);
                }
            });
}
