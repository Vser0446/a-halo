package com.l.ahalo.Item;

import com.l.ahalo.AHalo;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AHalo.MODID);
    public static final RegistryObject<Item> A_HALO =
            ITEMS.register("a_halo", () -> new AHaloItem(new Item.Properties()));
    public static final RegistryObject<Item> WEIGHT_ATTACHMENTS =
            ITEMS.register("weight_attachments", () -> new WeightAttachmentsItem(new Item.Properties()));
}
