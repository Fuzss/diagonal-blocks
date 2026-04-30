package fuzs.diagonalblocks.common.api.v2.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fuzs.diagonalblocks.common.api.v2.block.type.DiagonalBlockType;
import fuzs.diagonalblocks.common.impl.client.resources.model.MultiPartAppender;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.multipart.Selector;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;
import java.util.Map;

public class MultiPartTranslator {
    private static final Map<DiagonalBlockType, MultiPartTranslator> TRANSLATORS = Maps.newConcurrentMap();

    protected final DiagonalBlockType diagonalBlockType;

    protected MultiPartTranslator(DiagonalBlockType diagonalBlockType) {
        this.diagonalBlockType = diagonalBlockType;
    }

    public static void register(DiagonalBlockType diagonalBlockType, MultiPartTranslator multiPartTranslator) {
        TRANSLATORS.put(diagonalBlockType, multiPartTranslator);
    }

    public static MultiPartTranslator get(DiagonalBlockType diagonalBlockType) {
        return TRANSLATORS.computeIfAbsent(diagonalBlockType, MultiPartTranslator::new);
    }

    protected Comparable<?> getNewPropertyValue(Property<?> oldProperty, Property<?> newProperty, Comparable<?> oldValue) {
        return oldValue;
    }

    public BlockStateModelDispatcher.MultiPartDefinition apply(BlockStateModelDispatcher.MultiPartDefinition baseBlockModel) {
        return this.applyAdditionalSelectors(this.getModelFromBase(baseBlockModel));
    }

    protected BlockStateModelDispatcher.MultiPartDefinition getModelFromBase(BlockStateModelDispatcher.MultiPartDefinition multiPart) {
        List<Selector> selectors = Lists.newArrayList(multiPart.selectors());
        return new BlockStateModelDispatcher.MultiPartDefinition(selectors);
    }

    protected BlockStateModelDispatcher.MultiPartDefinition applyAdditionalSelectors(BlockStateModelDispatcher.MultiPartDefinition multiPart) {
        return MultiPartAppender.appendDiagonalSelectors(multiPart, false);
    }

    public boolean allowBaseModelAsFallback() {
        return true;
    }

    @Override
    public String toString() {
        return "MultiPartTranslator[" + this.diagonalBlockType + "]";
    }
}
