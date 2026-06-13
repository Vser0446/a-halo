package com.l.ahalo.Handler;

import com.l.ahalo.AHalo;
import com.l.ahalo.Item.ItemRegistry;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = AHalo.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttributeClampHandler {

    private static final UUID STEP_UUID = UUID.fromString("12345678-1234-1234-1234-123456789001");
    private static final UUID SPEED_UUID = UUID.fromString("12345678-1234-1234-1234-123456789002");
    private static final UUID SWIM_UUID = UUID.fromString("12345678-1234-1234-1234-123456789003");

    private static final double MAX_STEP = 0.4;
    private static final double MAX_SPEED = 0.3;
    private static final double MAX_SWIM = 3.0;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide)
            return;

        Player player = event.player;
        boolean isWearweights = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(handler -> handler.findFirstCurio(ItemRegistry.WEIGHT_ATTACHMENTS.get()))
                .isPresent();
        boolean isWearahalo = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(handler -> handler.findFirstCurio(ItemRegistry.A_HALO.get()))
                .isPresent();

        if ((!isWearahalo) && (!isWearweights)){
            // 没有佩戴 → 移除所有钳制修饰符，让属性恢复正常
            removeClamps(player);
            return;
        }

        // 佩戴了饰品 → 施加钳制
        clampStep(player);
        clampAttribute(player, Attributes.MOVEMENT_SPEED, MAX_SPEED, SPEED_UUID);
        clampAttribute(player, ForgeMod.SWIM_SPEED.get(), MAX_SWIM, SWIM_UUID);
    }

    private static void removeClamps(Player player) {
        AttributeInstance step = player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
        if (step != null) step.removeModifier(STEP_UUID);

        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) speed.removeModifier(SPEED_UUID);

        AttributeInstance swim = player.getAttribute(ForgeMod.SWIM_SPEED.get());
        if (swim != null) swim.removeModifier(SWIM_UUID);
    }

    private static void clampStep(Player player) {
        AttributeInstance attr = player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
        if (attr == null) return;

        attr.removeModifier(STEP_UUID);
        double current = attr.getValue();

        if (current > MAX_STEP) {
            double correction = MAX_STEP - current;
            AttributeModifier mod = new AttributeModifier(
                    STEP_UUID,
                    "Step clamp",
                    correction,
                    AttributeModifier.Operation.ADDITION
            );
            attr.addPermanentModifier(mod);
        }
    }

    private static void clampAttribute(Player player, Attribute attribute, double maxValue, UUID uuid) {
        AttributeInstance attr = player.getAttribute(attribute);
        if (attr == null) return;

        attr.removeModifier(uuid);
        double current = attr.getValue();

        if (current > maxValue) {
            double factor = (maxValue / current) - 1.0;
            AttributeModifier mod = new AttributeModifier(
                    uuid,
                    "Value clamp",
                    factor,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            attr.addPermanentModifier(mod);
        }
    }
}