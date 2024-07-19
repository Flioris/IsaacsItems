package flioris.isaacsitems.spirit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

// Killed player with Spirit Shackles.
@AllArgsConstructor
@Getter
public class Spirit {
    private final Location deathLocation;
    @Setter
    private boolean isRunning;
}
