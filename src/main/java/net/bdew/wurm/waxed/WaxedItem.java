package net.bdew.wurm.waxed;

import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.shared.constants.IconConstants;
import com.wurmonline.shared.util.MaterialUtilities;
import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;
import org.gotti.wurmunlimited.modsupport.items.ModItems;

import java.io.IOException;

public class WaxedItem {
    public static ItemTemplate waxedItem;
    public static int waxedItemId;

    public static void register() throws IOException {
        waxedItem = new ItemTemplateBuilder("bdew.waxed")
                .name("waxed item", "waxed items", "A piece of food that was waxed to preserve it.")
                .imageNumber((short) IconConstants.ICON_BEESWAX)
                .weightGrams(100)
                .dimensions(1, 1, 1)
                .decayTime(Long.MAX_VALUE)
                .itemTypes(new short[]{
                        ItemTypes.ITEM_TYPE_REPAIRABLE,
                        ItemTypes.ITEM_TYPE_DECORATION,
                        ItemTypes.ITEM_TYPE_TURNABLE,
                        ItemTypes.ITEM_TYPE_NAMED,
                        ItemTypes.ITEM_TYPE_COLORABLE,
                        ItemTypes.ITEM_TYPE_PLANTABLE
                })
                .modelName("model.resource.beeswax.")
                .behaviourType((short) 1)
                .build();

        waxedItemId = waxedItem.getTemplateId();

        ModItems.addModelNameProvider(waxedItemId, item -> {
            if (item.getRealTemplate() != null) {
                return item.getRealTemplate().getModelName() + MaterialUtilities.getMaterialString(item.getMaterial());
            } else return item.getTemplate().getModelName();
        });
    }
}
