package fuzs.diagonalblocks.fabric.impl;

import fuzs.diagonalblocks.common.impl.DiagonalBlocks;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class DiagonalBlocksFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(DiagonalBlocks.MOD_ID, DiagonalBlocks::new);
    }
}
