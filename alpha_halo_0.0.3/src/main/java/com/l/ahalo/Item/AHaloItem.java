package com.l.ahalo.Item;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;


public class AHaloItem extends Item implements ICurioItem {
    public AHaloItem(Properties properties ) {
        super(properties.stacksTo(1));
    }
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn,
                                List<Component> list, TooltipFlag flagIn) {
        ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.void");
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.a_halos1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.a_halos2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.a_halos3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.a_halos4");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.a_halos5");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.a_halos6");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.a_halos7");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.a_halos8");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.void");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.a_halod1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.a_halod2");
            if (Minecraft.getInstance().player != null && SuperpositionHandler.canUnequipBoundRelics(Minecraft.getInstance().player)) {
                ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.a_haloc1");
            } else {
                ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.void");
            }
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.a_halod3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.void");
        }

    }
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        // 确保只在服务端执行（避免客户端修改不同步）
        if (!slotContext.entity().level().isClientSide) {
            if (slotContext.entity() instanceof Player player) {
                FoodData foodData = player.getFoodData();
                // 如果当前饱食度 >= 16，则降低到 15
                if (foodData.getFoodLevel() > 16) {
                    foodData.setFoodLevel(16);
                }
            }
        }
    }
}
