package fuzs.diagonalblocks.common.impl.client.resources.translator;

import fuzs.diagonalblocks.common.api.v2.block.type.DiagonalBlockTypes;
import fuzs.diagonalblocks.common.api.v2.client.MultiPartTranslator;
import fuzs.diagonalblocks.common.impl.client.resources.model.MultiPartAppender;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;

public final class WindowMultiPartTranslator extends MultiPartTranslator {

    public WindowMultiPartTranslator() {
        super(DiagonalBlockTypes.WINDOW);
    }

    @Override
    protected BlockStateModelDispatcher.MultiPartDefinition applyAdditionalSelectors(BlockStateModelDispatcher.MultiPartDefinition multiPart) {
        return MultiPartAppender.appendDiagonalSelectors(multiPart, true);
    }
}
