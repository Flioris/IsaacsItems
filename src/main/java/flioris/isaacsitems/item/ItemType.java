package flioris.isaacsitems.item;

import flioris.isaacsitems.util.ConfigHandler;
import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum ItemType {
    THE_HIEROPHANT(1), THE_LOVERS(2), THE_CHARIOT(3), THE_TOWER(4), BAG_LUNCH(5), THE_INTRUDER(6), ROCK_BOTTOM(7),
    SOCKS(8), MONSTRANCE(9), SPIRIT_SHACKLES(10);

    private final int customModelData;
    @Getter
    private static final Material material = Material.getMaterial(ConfigHandler.getString("material"));

    ItemType(int customModelData) {
        this.customModelData = customModelData;
    }

    public static ItemType getFromCustomModelData(int customModelData) {
        for (ItemType itemType : ItemType.values()) {
            if (itemType.customModelData == customModelData) {
                return itemType;
            }
        }

        return null;
    }
}
