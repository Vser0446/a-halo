package com.l.ahalo.Handler;

import com.l.ahalo.AHalo;
import com.l.ahalo.Item.ItemRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = AHalo.MODID)  // 替换为你的 MOD ID
public class HurtEventHandler {

    // 记录每个玩家上次造成伤害的服务器 tick（20 ticks = 1 秒）
    private static final ConcurrentHashMap<UUID, Long> LAST_ATTACK_TICK = new ConcurrentHashMap<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        // 仅在服务端执行逻辑
        if (event.getEntity().level().isClientSide) {
            return;
        }

        DamageSource source = event.getSource();
        LivingEntity target = event.getEntity();

        // 1. 获取真正的玩家攻击者
        Player attacker = getPlayerAttacker(source);
        if (attacker == null) {
            return; // 不是玩家造成的伤害，不处理
        }

        boolean isWearahalo = CuriosApi.getCuriosInventory(attacker).resolve()
                .flatMap(handler -> handler.findFirstCurio(ItemRegistry.A_HALO.get()))
                .isPresent();

        if (!isWearahalo) {
            return;
        }

        // 1 秒内最多造成一次伤害 =
        long currentTick = attacker.level().getGameTime();
        UUID attackerId = attacker.getUUID();
        Long lastTick = LAST_ATTACK_TICK.get(attackerId);

        if (lastTick != null && (currentTick - lastTick) < 20) {
            // 距离上次攻击不足 1 秒，取消本次伤害
            event.setCanceled(true);
            return;
        }
        // 记录本次攻击的 tick
        LAST_ATTACK_TICK.put(attackerId, currentTick);

        // 玩家造成的最终伤害不超过 1
        float originalDamage = event.getAmount();
        float limitedDamage = Math.min(originalDamage, 1.0F);
        event.setAmount(limitedDamage);
    }


    private static Player getPlayerAttacker(DamageSource source) {
        // 直接攻击者
        if (source.getEntity() instanceof Player player) {
            return player;
        }
        // 弹射物攻击（箭、火球、雪球等）
        if (source.getDirectEntity() instanceof Projectile projectile &&
                projectile.getOwner() instanceof Player player) {
            return player;
        }
        return null;
    }
}
