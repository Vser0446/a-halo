package com.l.ahalo.Item;

import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;


public class WeightAttachmentsItem extends Item implements ICurioItem {
    public WeightAttachmentsItem(Properties properties ) {
        super(properties.stacksTo(1));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> list, TooltipFlag flagIn) {
        ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.void");
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.weight_attachments1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.weight_attachments2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.weight_attachments3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.weight_attachments4");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.weight_attachments5");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.weight_attachments6");
        }
        else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.weight_attachmentsd1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.weight_attachmentsd2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.weight_attachmentsd3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.a_halo.weight_attachmentsd4");
        }
    }
}
