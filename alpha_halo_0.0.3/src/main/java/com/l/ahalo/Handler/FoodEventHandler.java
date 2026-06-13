package com.l.ahalo.Handler;

import com.l.ahalo.AHalo;
import com.l.ahalo.Item.ItemRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = AHalo.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FoodEventHandler {
    private static final Map<UUID, Integer> OVER_EAT_COUNTER = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerEat(LivingEntityUseItemEvent.@NotNull Finish event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }

        boolean isWearahalo = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(handler -> handler.findFirstCurio(ItemRegistry.A_HALO.get()))
                .isPresent();

        if (!isWearahalo) {
            OVER_EAT_COUNTER.remove(player.getUUID());
            return;
        }

        FoodData foodData = player.getFoodData();
        foodData.setSaturation(0);
        if (foodData.getFoodLevel() > 16)  {
            foodData.setFoodLevel(16);
            UUID id = player.getUUID();
            int count = OVER_EAT_COUNTER.getOrDefault(id, 0) + 1;
            OVER_EAT_COUNTER.put(id, count);

            if (count >= 5) {
                // 发送文字提醒
                player.displayClientMessage(Component.translatable("message.a_halo.cant_eat"), true);
                if (player instanceof ServerPlayer serverPlayer) {
                    // 移除服务端效果
                    player.removeEffect(MobEffects.CONFUSION);
                    // 强制同步空效果到客户端（清理幽灵buff）
                    serverPlayer.connection.send(new ClientboundRemoveMobEffectPacket(
                            player.getId(),
                            MobEffects.CONFUSION
                    ));
                }
                // 施加5秒1级反胃
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 5 * 20, 0));
                // 重置计数器，从头开始累积下一次惩罚
                OVER_EAT_COUNTER.put(id, 0);
            }
        }
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(
                    new ClientboundSetHealthPacket(
                            serverPlayer.getHealth(),
                            serverPlayer.getFoodData().getFoodLevel(),
                            serverPlayer.getFoodData().getSaturationLevel()
                    )
            );
        }
    }
}