package net.toarupgm.griefpreventionmapper.client.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.toarupgm.griefpreventionmapper.client.PlayerAreaMapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onBlockUpdate", at = @At("HEAD"))
    public void onBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo ci) {
        BlockPos pos = packet.getPos();
        BlockState state = packet.getState();
        if(state.getBlock() == Blocks.GLOWSTONE)
        {
            PlayerAreaMapper.INSTANCE.recordCorner(pos);
        }
    }
}
