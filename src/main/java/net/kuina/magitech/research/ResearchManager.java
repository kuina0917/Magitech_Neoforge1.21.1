package net.kuina.magitech.research;

import net.kuina.magitech.item.MagitechItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;

/**
 * 研究ツリーの管理クラス。
 */
public class ResearchManager {
    private static final Map<String, ResearchNode> NODES = new HashMap<>();

    static {
        // サンプルの研究ツリー構築
        addNode(new ResearchNode("root", "research.magitech.root", new ItemStack(MagitechItems.MANA_STONE.get()), 0, 0));
        
        addNode(new ResearchNode("mana_basics", "research.magitech.mana_basics", new ItemStack(MagitechItems.LOW_MANA_INGOT.get()), 60, -30)
                .addParent("root"));
        
        addNode(new ResearchNode("processing", "research.magitech.processing", new ItemStack(MagitechItems.MANA_PROCESSOR.get()), 120, 0)
                .addParent("mana_basics"));
        
        addNode(new ResearchNode("advanced_tools", "research.magitech.advanced_tools", new ItemStack(MagitechItems.LOW_MANA_PICKAXE.get()), 120, -60)
                .addParent("mana_basics"));

        addNode(new ResearchNode("storage", "research.magitech.storage", new ItemStack(MagitechItems.MANA_TANK.get()), 60, 30)
                .addParent("root"));
    }

    private static void addNode(ResearchNode node) {
        NODES.put(node.getId(), node);
    }

    public static Map<String, ResearchNode> getNodes() {
        return NODES;
    }
}
