package com.cortezromeo.taixiu.support.version.v1_16_R3;

import com.cortezromeo.taixiu.api.server.VersionSupport;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class v1_16_R3 extends VersionSupport {

    public v1_16_R3(Plugin plugin, String versionName) {
        super(plugin, versionName);
    }

    public int getVersion() {
        return 13;
    }

    @Override
    public ItemStack createItemStack(String material, int amount, short data) {
        ItemStack i;
        try {
            i = new ItemStack(Material.valueOf(material), amount, data);
        } catch (Exception ex) {
            getPlugin().getLogger().severe("----------------------------------------------------");
            getPlugin().getLogger().severe("MATERIAL " + material + " KHÔNG HỢP LỆ!");
            getPlugin().getLogger().severe(">> Link Materials cho 1.16.5 <<");
            getPlugin().getLogger().severe("https://helpch.at/docs/1.16.5/org/bukkit/Material.html");
            getPlugin().getLogger().severe("----------------------------------------------------");
            i = new ItemStack(Material.BEDROCK);
        }
        return i;
    }

    @Override
    public ItemStack getHeadItem() {
        return new ItemStack(Material.PLAYER_HEAD);
    }

    @Override
    public ItemStack addCustomData(ItemStack i, String data) {
        net.minecraft.server.v1_16_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(i);
        NBTTagCompound tag = itemStack.getTag();
        if (tag == null) {
            tag = new NBTTagCompound();
            itemStack.setTag(tag);
        }

        tag.setString("TaiXiu", data);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public String getCustomData(ItemStack i) {
        net.minecraft.server.v1_16_R3.ItemStack itemStack = CraftItemStack.asNMSCopy(i);
        NBTTagCompound tag = itemStack.getTag();
        if (tag == null) return "";
        return tag.getString("TaiXiu");
    }

    private final Pattern HEX_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-fA-F])");

    public String addColor(String textToTranslate) {

        if (textToTranslate == null)
            return "NULL";

        Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    private Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf('&') + "[0-9A-FK-OR]");

    public String stripColor(String textToStrip) {
        return textToStrip == null ? null : STRIP_COLOR_PATTERN.matcher(textToStrip).replaceAll("");
    }
}
