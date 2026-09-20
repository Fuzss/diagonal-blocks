package fuzs.diagonalblocks.neoforge.impl;

import fuzs.diagonalblocks.common.impl.DiagonalBlocks;
import fuzs.diagonalblocks.common.impl.data.tags.ModBlockTagsProvider;
import fuzs.diagonalblocks.neoforge.integration.cfm.MrCrayfishFurnitureMod;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v3.core.DataProviderBuilder;
import net.neoforged.fml.common.Mod;

@Mod(DiagonalBlocks.MOD_ID)
public class DiagonalBlocksNeoForge {

    public DiagonalBlocksNeoForge() {
        ModConstructor.construct(DiagonalBlocks.MOD_ID, DiagonalBlocks::new);
        DataProviderBuilder.of(DiagonalBlocks.MOD_ID).addProvider(ModBlockTagsProvider::new);
        registerModIntegration();
    }

    private static void registerModIntegration() {
        MrCrayfishFurnitureMod.init();
    }
}
