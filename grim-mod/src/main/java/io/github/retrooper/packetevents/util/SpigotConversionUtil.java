package io.github.retrooper.packetevents.util;

import ac.grim.grimac.GrimAPI;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTLimiter;
import com.github.retrooper.packetevents.protocol.nbt.serializer.DefaultNBTSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class SpigotConversionUtil {
    public static com.github.retrooper.packetevents.protocol.item.ItemStack fromBukkitItemStack(ItemStack itemStack) {
        /*DynamicRegistryManager.Immutable registryManager = GrimAPI.INSTANCE.getServer().getRegistryManager();
        NbtCompound minecraftNbt = (NbtCompound) itemStack.encode(registryManager);

        NBTCompound key;
        try (ByteArrayInputStream stream = new ByteArrayInputStream(bin.getData()); DataInputStream in = new DataInputStream(stream)) {
            key = (NBTCompound) DefaultNBTSerializer.INSTANCE.deserializeTag(NBTLimiter.noop(), in, false);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load legacy block mappings", e);
        }*/

        //TODO Components

        Identifier id = Registries.ITEM.getId(itemStack.getItem());

        return com.github.retrooper.packetevents.protocol.item.ItemStack.builder()
                .amount(itemStack.getCount())
                .type(ItemTypes.getRegistry().getByName(id.toString()))
                .build();
    }
}
