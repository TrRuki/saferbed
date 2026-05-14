package com.trruki.saferbed.mixin;
import com.trruki.saferbed.client.SaferbedClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Inject(
            method = "useItemOn",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onUseItemOn(LocalPlayer localPlayer, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!SaferbedClient.enabled) return;

        BlockPos pos = blockHitResult.getBlockPos();
        BlockState blockState = localPlayer.level().getBlockState(pos);

        if(!(blockState.getBlock() instanceof BedBlock)) return;

        ResourceKey<Level> dimension = localPlayer.level().dimension();
        if (dimension == Level.NETHER || dimension == Level.END) {
            cir.setReturnValue(InteractionResult.FAIL);
            Minecraft.getInstance().gui.setOverlayMessage(Component.literal("§cSafer Bed: prevented from dying §8| §7Toggle key: §f" + SaferbedClient.toggleKey.getTranslatedKeyMessage().getString()), false);
        }
    }
}
