package com.aguga.Utils;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface ChestClosedCallback
{
    void invoke(PlayerEntity player);

    Event<ChestClosedCallback> EVENT = EventFactory.createArrayBacked(ChestClosedCallback.class,
            (listeners) -> (player) -> {
                for (ChestClosedCallback listener : listeners) {
                    // Invoke all event listeners with the provided player and death message.
                    listener.invoke(player);
                }
            });
}
