package com.l.ahalo.Handler;

import com.l.ahalo.AHalo;
import com.l.ahalo.Item.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = AHalo.MODID)
public class BreakSpeedHandler {
    private static final Map<UUID, Integer> HARD_BREAK_COUNTER = new HashMap<>();

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();

        boolean isWearweights = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(handler -> handler.findFirstCurio(ItemRegistry.WEIGHT_ATTACHMENTS.get()))
                .isPresent();
        boolean isWearahalo = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(handler -> handler.findFirstCurio(ItemRegistry.A_HALO.get()))
                .isPresent();

        if ((!isWearahalo) && (!isWearweights)){
            HARD_BREAK_COUNTER.remove(player.getUUID());
            return;
        }

        Optional<BlockPos> posOpt = event.getPosition();
        if (posOpt.isEmpty()) return;
        BlockPos pos = posOpt.get();

        BlockState state = event.getState();
        float hardness = state.getDestroySpeed(player.level(), pos);
        if ((hardness < 0) && (!isWearahalo)) return;

        float effectiveHardness = Math.max(hardness, 0.001F);
        float currentSpeed = event.getNewSpeed();
        float maxSpeed = 1.5F * effectiveHardness;

        // 当前速度已经足够慢（>=1s），不处理
        if (currentSpeed <= maxSpeed) return;

        if (isWearahalo) {
            UUID id = player.getUUID();
            int count = HARD_BREAK_COUNTER.getOrDefault(id, 0) + 1;
            HARD_BREAK_COUNTER.put(id, count);

            if (count >= 1314) {
                // 发送文字提醒
                player.displayClientMessage(Component.translatable("message.a_halo.dig_slowdown"), true);

                // 施加5秒1级挖掘疲劳
               // player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 5 * 20, 0));

                // 重置计数器，从头开始累积下一次惩罚
                HARD_BREAK_COUNTER.put(id, 0);
            }
        }

        event.setNewSpeed(maxSpeed);

    }
}