package flioris.isaacsitems.data;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import flioris.isaacsitems.spirit.Spirit;
import lombok.Getter;

import java.util.*;

public class PlayerData {
    @Getter
    private static final Multiset<UUID> chariotPlayers = HashMultiset.create();
    @Getter
    private static final Multiset<UUID> towerPlayers = HashMultiset.create();
    @Getter
    private static final Map<UUID, Spirit> killedPlayers = new HashMap<>();
}
