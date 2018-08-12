package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Item;

import java.util.LinkedHashSet;
import java.util.Set;

public final class Items {

    public static Items newItemSet() {
        return new Items();
    }

    private final Set<Item> items = new LinkedHashSet<>();

    private Items() {
    }

    public Item.Builder newItem() {
        return new Item.Builder() {
            @Override
            public Item build() {
                Item item = super.build();
                items.add(item);
                return item;
            }
        };
    }

    public Set<Item> copyOfItems() {
        return new LinkedHashSet<>(items);
    }

}
