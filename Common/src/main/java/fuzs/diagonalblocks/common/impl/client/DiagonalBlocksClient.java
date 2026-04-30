package fuzs.diagonalblocks.common.impl.client;

import fuzs.diagonalblocks.common.api.v2.block.type.DiagonalBlockType;
import fuzs.diagonalblocks.common.api.v2.block.type.DiagonalBlockTypes;
import fuzs.diagonalblocks.common.api.v2.client.MultiPartTranslator;
import fuzs.diagonalblocks.common.impl.client.handler.DiagonalModelHandler;
import fuzs.diagonalblocks.common.impl.client.resources.translator.WallMultiPartTranslator;
import fuzs.diagonalblocks.common.impl.client.resources.translator.WindowMultiPartTranslator;
import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.common.api.client.core.v1.context.BlockStateResolverContext;
import fuzs.puzzleslib.common.api.client.renderer.v1.model.ModelLoadingHelper;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

public class DiagonalBlocksClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        // this cannot happen later during client setup,
        // as model loading will already have begun by then in the background
        MultiPartTranslator.register(DiagonalBlockTypes.WINDOW, new WindowMultiPartTranslator());
        MultiPartTranslator.register(DiagonalBlockTypes.WALL, new WallMultiPartTranslator());
    }

    @Override
    public void onRegisterBlockStateResolver(BlockStateResolverContext context) {
        for (DiagonalBlockType diagonalBlockType : DiagonalBlockType.TYPES) {
            MultiPartTranslator multiPartTranslator = MultiPartTranslator.get(diagonalBlockType);
            diagonalBlockType.getBlockConversions().forEach((Block oldBlock, Block newBlock) -> {
                context.registerBlockStateResolver(newBlock,
                        (ResourceManager resourceManager, Executor executor) -> {
                            return CompletableFuture.supplyAsync(() -> resourceManager.getResource(BlockStateModelLoader.BLOCKSTATE_LISTER.idToFile(
                                            BuiltInRegistries.BLOCK.getKey(newBlock))), executor)
                                    .thenCompose((Optional<Resource> optional) -> {
                                        if (optional.isPresent()) {
                                            return ModelLoadingHelper.loadBlockState(resourceManager,
                                                    newBlock,
                                                    executor);
                                        } else {
                                            return ModelLoadingHelper.loadBlockState(resourceManager,
                                                            BuiltInRegistries.BLOCK.getKey(oldBlock),
                                                            executor)
                                                    .thenApply((List<BlockStateModelLoader.LoadedBlockStateModelDispatcher> loadedBlockModelDefinitions) -> {
                                                        return DiagonalModelHandler.transformLoadedBlockStateModelDispatchers(
                                                                loadedBlockModelDefinitions,
                                                                multiPartTranslator,
                                                                () -> {
                                                                    DiagonalModelHandler.reportInvalidBlockModel(
                                                                            BuiltInRegistries.BLOCK.getKey(oldBlock),
                                                                            diagonalBlockType);
                                                                });
                                                    })
                                                    .thenCompose((List<BlockStateModelLoader.LoadedBlockStateModelDispatcher> loadedBlockModelDefinitions) -> {
                                                        return ModelLoadingHelper.loadBlockState(
                                                                loadedBlockModelDefinitions,
                                                                BuiltInRegistries.BLOCK.getKey(newBlock),
                                                                newBlock.getStateDefinition(),
                                                                executor);
                                                    });
                                        }
                                    });
                        },
                        (BlockStateModelLoader.LoadedModels loadedModels, BiConsumer<BlockState, BlockStateModel.UnbakedRoot> blockStateConsumer) -> {
                            for (BlockState blockState : newBlock.getStateDefinition().getPossibleStates()) {
                                if (loadedModels.models().containsKey(blockState)) {
                                    blockStateConsumer.accept(blockState, loadedModels.models().get(blockState));
                                } else {
                                    blockStateConsumer.accept(blockState, ModelLoadingHelper.missingModel());
                                }
                            }
                        });
            });
        }
    }
}
