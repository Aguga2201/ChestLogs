package com.aguga.mixin;

import com.aguga.Utils.ChestClosedCallback;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityMixin
{
    @Inject(at = @At("TAIL"), method = "onClose")
    public void onClose(PlayerEntity player, CallbackInfo ci)
    {
        ChestClosedCallback.EVENT.invoker().invoke(player);
    }
}
