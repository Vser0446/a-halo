package com.l.ahalo.Handler;

import com.l.ahalo.AHalo;
import com.l.ahalo.Item.ItemRegistry;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = AHalo.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SleepEventHandler {

    private static boolean hasNearbyLivingEntities(Player player) {
        AABB searchArea = player.getBoundingBox().inflate(8.0);
        return !player.level().getEntitiesOfClass(
                LivingEntity.class,
                searchArea,
                e -> e != player && e.isAlive()
        ).isEmpty();
    }

    private static boolean isWearahalo(Player player) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(inv -> inv.findFirstCurio(ItemRegistry.A_HALO.get()))
                .isPresent();
    }

    // 记录当前“戴着特定○环且附近有实体”且正在睡觉的玩家UUID
    private static final Set<UUID> BLOCKED_SLEEPERS = new HashSet<>();

    @SubscribeEvent
    public static void onPlayerSleepInBed(PlayerSleepInBedEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        if (isWearahalo(player) && hasNearbyLivingEntities(player)) {
            BLOCKED_SLEEPERS.add(player.getUUID());
            player.sendSystemMessage(Component.translatable("message.a_halo.cannot_sleep_others_watching"));
            updateSleepPercentage((ServerLevel) player.level()); 
        }
    }

    @SubscribeEvent
    public static void onEntityWakeUp(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        if (BLOCKED_SLEEPERS.remove(player.getUUID())) {
            updateSleepPercentage((ServerLevel) player.level());
        }
    }

    private static void updateSleepPercentage(ServerLevel level) {
        GameRules.IntegerValue rule = level.getGameRules().getRule(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
        if (BLOCKED_SLEEPERS.isEmpty()) {
            rule.set(100, level.getServer());
        } else {
            rule.set(101, level.getServer());
        }
    }
}