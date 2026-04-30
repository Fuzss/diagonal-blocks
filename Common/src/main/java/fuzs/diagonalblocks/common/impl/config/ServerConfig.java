package fuzs.diagonalblocks.common.impl.config;

import fuzs.puzzleslib.common.api.config.v3.Config;
import fuzs.puzzleslib.common.api.config.v3.ConfigCore;

public class ServerConfig implements ConfigCore {
    @Config(description = "Swap between diagonal and non-diagonal block types via sneak+right-clicking with an empty hand.")
    public boolean swapBlocksByInteracting = true;
}
