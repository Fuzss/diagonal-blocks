package fuzs.diagonalblocks.common.impl.config;

import fuzs.puzzleslib.common.api.config.v3.Config;
import fuzs.puzzleslib.common.api.config.v3.ConfigCore;

public class CommonConfig implements ConfigCore {
    @Config(description = "When placing blocks use the diagonal variants instead of the non-diagonal blocks.",
            gameRestart = true)
    public boolean diagonalBlocksByDefault = true;
}
