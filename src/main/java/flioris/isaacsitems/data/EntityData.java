package flioris.isaacsitems.data;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EntityData {
    @Getter
    private static final Map<UUID, UUID> lastDamageByPlayer = new HashMap<>();
}
