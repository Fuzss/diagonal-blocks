package fuzs.diagonalblocks.common.impl.client.resources.model;

import fuzs.puzzleslib.common.api.client.renderer.v1.model.MutableBakedQuad;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public record RotatedVariant(BlockStateModel.Unbaked variant, Direction direction) implements BlockStateModel.Unbaked {
    static final float ROTATION_ANGLE = -45.0F * 0.017453292F;
    /**
     * Scale factor at a 45-degree rotation.
     */
    static final float SCALE_ROTATION_45 = 1.0F / (float) Math.cos(Math.PI / 4.0) - 1.0F;
    static final Vector3f ROTATION_ORIGIN = new Vector3f(0.5F, 0.5F, 0.5F);
    static final Matrix4f ROTATION_MATRIX = new Matrix4f().rotation(new Quaternionf().setAngleAxis(ROTATION_ANGLE,
            0.0F,
            1.0F,
            0.0F));
    static final Collection<Direction> VALID_QUAD_FACES = Util.make(new ArrayList<>(Arrays.asList(Direction.values())),
            (ArrayList<Direction> list) -> {
                list.add(null);
            });

    @Override
    public void resolveDependencies(Resolver resolver) {
        this.variant.resolveDependencies(resolver);
    }

    @Override
    public BlockStateModel bake(ModelBaker modelBaker) {
        Function<BlockStateModelPart, BlockStateModelPart> blockModelPartRotator = Util.memoize((BlockStateModelPart blockModelPart) -> {
            return rotateBlockStateModelPart(blockModelPart, this.direction);
        });
        BlockStateModel blockStateModel = this.variant.bake(modelBaker);
        return new BlockStateModel() {
            @Override
            public void collectParts(RandomSource randomSource, List<BlockStateModelPart> list) {
                List<BlockStateModelPart> tmpList = new ObjectArrayList<>();
                blockStateModel.collectParts(randomSource, tmpList);
                for (BlockStateModelPart blockModelPart : tmpList) {
                    list.add(blockModelPartRotator.apply(blockModelPart));
                }
            }

            @Override
            public Material.Baked particleMaterial() {
                return blockStateModel.particleMaterial();
            }

            @Override
            public @BakedQuad.MaterialFlags int materialFlags() {
                return blockStateModel.materialFlags();
            }
        };
    }

    /**
     * Duplicate and rotate all contained {@link BakedQuad BakedQuads} 45 degrees clockwise from the given
     * {@link BlockStateModelPart} to produce a new variant for a diagonal side.
     */
    private static BlockStateModelPart rotateBlockStateModelPart(BlockStateModelPart blockModelPart, Direction direction) {

        QuadCollection.Builder builder = new QuadCollection.Builder();
        for (Direction face : VALID_QUAD_FACES) {
            for (BakedQuad bakedQuad : blockModelPart.getQuads(face)) {

                MutableBakedQuad copiedBakedQuad = MutableBakedQuad.toMutable(bakedQuad);
                rotateQuad(copiedBakedQuad, direction);
                if (face == null) {
                    builder.addUnculledFace(copiedBakedQuad.toImmutable());
                } else {
                    builder.addCulledFace(face, copiedBakedQuad.toImmutable());
                }
            }
        }

        return new SimpleModelWrapper(builder.build(),
                blockModelPart.useAmbientOcclusion(),
                blockModelPart.particleMaterial());
    }

    /**
     * Rotate the given {@link BakedQuad} 45 degrees clockwise and recalculate its vertex normals.
     *
     * @author <a href="https://github.com/XFactHD">XFactHD</a>
     */
    private static void rotateQuad(MutableBakedQuad bakedQuad, Direction direction) {

        Vector3f scaleMultiplier = new Vector3f(Math.abs(direction.getStepX()), 1.0F, Math.abs(direction.getStepZ()));
        Vector3f scaleVector = new Vector3f(1.0F, 0.0F, 1.0F);
        scaleVector.mul(SCALE_ROTATION_45);
        scaleVector.mul(scaleMultiplier.x(), scaleMultiplier.y(), scaleMultiplier.z());
        scaleVector.add(1.0F, 1.0F, 1.0F);

        for (int i = 0; i < 4; i++) {
            Vector3f positionVector = new Vector3f(bakedQuad.position(i));
            positionVector.sub(ROTATION_ORIGIN).mul(scaleVector);
            Vector4f transformVector = new Vector4f(positionVector, 1.0F);
            ROTATION_MATRIX.transform(transformVector);
            positionVector.set(transformVector).add(ROTATION_ORIGIN);
            bakedQuad.position(i, positionVector);
        }

        bakedQuad.computeQuadNormals();
    }
}
