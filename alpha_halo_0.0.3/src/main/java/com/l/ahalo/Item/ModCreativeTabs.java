package com.l.ahalo.Item;

import com.l.ahalo.AHalo;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AHalo.MODID);
    public static final RegistryObject<CreativeModeTab> A_RING_TAB =
            CREATIVE_MODE_TABS.register("alpha_halo_tab",() -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ItemRegistry.A_HALO.get()))
                    .title(Component.translatable("Alpha Halo"))
                    .displayItems((pParameters, pOutput) ->{
                        pOutput.accept(ItemRegistry.A_HALO.get());
                        pOutput.accept(ItemRegistry.WEIGHT_ATTACHMENTS.get());
                            })
                    .build()
            );

}
