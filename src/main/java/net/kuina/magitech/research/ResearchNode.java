package net.kuina.magitech.research;

import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

/**
 * 研究ノード（クエスト）のデータクラス。
 */
public class ResearchNode {
    private final String id;
    private final String titleKey;
    private final ItemStack icon;
    private final int x, y;
    private final List<String> parents = new ArrayList<>();

    public ResearchNode(String id, String titleKey, ItemStack icon, int x, int y) {
        this.id = id;
        this.titleKey = titleKey;
        this.icon = icon;
        this.x = x;
        this.y = y;
    }

    public ResearchNode addParent(String parentId) {
        this.parents.add(parentId);
        return this;
    }

    public String getId() { return id; }
    public String getTitleKey() { return titleKey; }
    public ItemStack getIcon() { return icon; }
    public int getX() { return x; }
    public int getY() { return y; }
    public List<String> getParents() { return parents; }
}
