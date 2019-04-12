package net.bdew.wurm.waxed;

import com.wurmonline.server.items.ItemTemplateFactory;
import org.gotti.wurmunlimited.modsupport.IdFactory;
import org.gotti.wurmunlimited.modsupport.IdType;
import org.gotti.wurmunlimited.modsupport.items.ItemIdParser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdvancedItemIdParser extends ItemIdParser {
    private Map<String, Integer> idMap = new HashMap<>();

    public AdvancedItemIdParser() {
        IdFactory.getIdsFor(IdType.ITEMTEMPLATE).forEach(e -> idMap.put(e.getKey(), e.getValue()));
    }

    @Override
    protected int unparsable(String name) {
        Integer res = idMap.get(name);
        if (res != null) {
            return res;
        } else {
            return super.unparsable(name);
        }
    }

    public Stream<Integer> parseSafe(String name) {
        try {
            return Stream.of(parse(name));
        } catch (Exception e) {
            WaxedMod.logWarning(String.format("Error parsing item id '%s': %s", name, e.toString()));
            return Stream.empty();
        }
    }

    public Set<Integer> parseListSafe(String str) {
        return Arrays.stream(str.split(","))
                .map(String::trim)
                .flatMap(this::parseSafe)
                .filter(i -> ItemTemplateFactory.getInstance().getTemplateOrNull(i) != null)
                .collect(Collectors.toSet());
    }

}
