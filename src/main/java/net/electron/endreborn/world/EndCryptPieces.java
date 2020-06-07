package net.electron.endreborn.world;

import com.google.common.collect.ImmutableMap;
import net.electron.endreborn.EndReborn;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.*;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.IglooFeature;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class EndCryptPieces {

    private static final Identifier TOP = new Identifier(EndReborn.MODID + ":end_crypt_top");
    private static final Identifier FRONT = new Identifier(EndReborn.MODID + ":end_crypt_front");
    private static final Identifier CROSS = new Identifier(EndReborn.MODID + ":end_crypt_cross");
    private static final Identifier ROOM = new Identifier(EndReborn.MODID + ":end_crypt_room");
    private static final Identifier LEFT = new Identifier(EndReborn.MODID + ":end_crypt_left");

    private static final Map<Identifier, BlockPos> OFFSET = ImmutableMap.of(TOP, new BlockPos(0, 1, 0), FRONT, new BlockPos(0, 1, 0), LEFT, new BlockPos(0, 1, 0), CROSS, new BlockPos(0, 1, 0), ROOM, new BlockPos(0, 1, 0));


    /*
     * Begins assembling your structure and where the pieces needs to go.
     */
    public static void addPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, List<StructurePiece> pieces, Random random) {
        int x = pos.getX();
        int z = pos.getZ();
        int r = random.nextInt(6) + 2;
        int c = 0;

        BlockPos rotationOffSet = new BlockPos(0, 0, 0).rotate(rotation);
        BlockPos blockpos = rotationOffSet.add(x, pos.getY(), z);
        pieces.add(new EndCryptPieces.Piece(manager, TOP, blockpos, rotation, 0));
        for (int j = r; j > 0; --j) {
            int r1 = random.nextInt(13);
            if (r1 >= 0 && r1 < 7) {
                c = c+1;
                rotationOffSet = new BlockPos(0, 0, -7 * c).rotate(rotation);
                blockpos = rotationOffSet.add(x, pos.getY(), z);
                pieces.add(new EndCryptPieces.Piece(manager, FRONT, blockpos, rotation, 0));
            } else if (r1 >= 7 && r1 < 13) {
                c = c+1;
                int t = random.nextInt(7);
                int r2 = random.nextInt(10);
                int r3 = random.nextInt(4);
                int c1 = 0;
                int c2 = 0;

                rotationOffSet = new BlockPos(0, 0, -7 * c).rotate(rotation);
                blockpos = rotationOffSet.add(x, pos.getY(), z);
                pieces.add(new EndCryptPieces.Piece(manager, CROSS, blockpos, rotation, 0));
                if (r3 >= 3) {
                    for (int k = t; k > 0; --k) {
                        if (r2 >= 1 && r2 < 5) {
                            c1 = c1 + 1;
                            rotationOffSet = new BlockPos(-7 * c1, 0, -7 * c).rotate(rotation);
                            blockpos = rotationOffSet.add(x, pos.getY(), z);
                            pieces.add(new EndCryptPieces.Piece(manager, LEFT, blockpos, rotation, 0));
                        } else if (r2 >= 7 && r2 <= 10) {
                            c1 = c1 + 1;
                            rotationOffSet = new BlockPos(-7 * c1, 0, -7 * c).rotate(rotation);
                            blockpos = rotationOffSet.add(x, pos.getY(), z);
                            pieces.add(new EndCryptPieces.Piece(manager, ROOM, blockpos, rotation, 0));
                            k = 0;
                        }
                    }
                } else if (r3 < 3){
                    int r4 = random.nextInt(5);
                    for (int i = t; i > 0; --i) {
                        if (r4 > 1) {
                            c2 = c2 + 1;
                            rotationOffSet = new BlockPos(7 * c2, 0, -7 * c).rotate(rotation);
                            blockpos = rotationOffSet.add(x, pos.getY(), z);
                            pieces.add(new EndCryptPieces.Piece(manager, LEFT, blockpos, rotation, 0));
                        } else if (r4 <=1){
                            c2 = c2 + 1;
                            rotationOffSet = new BlockPos(7 * c2, 0, -7 * c).rotate(rotation);
                            blockpos = rotationOffSet.add(x, pos.getY(), z);
                            pieces.add(new EndCryptPieces.Piece(manager, ROOM, blockpos, rotation, 0));
                            i = 0;
                        }
                    }
                }
            }
        }
        c = 0;
    }

    public static class Piece extends SimpleStructurePiece
    {
        private final Identifier template;
        private final BlockRotation rotation;

        public Piece(StructureManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation, int yOffset) {
            super(NatureStructures.END_CRYPT_PIECE, 0);
            this.template = identifier;
            BlockPos blockpos = (BlockPos)EndCryptPieces.OFFSET.get(identifier);
            this.pos = pos.add(blockpos.getX(), blockpos.getY(), blockpos.getZ());
            this.rotation = rotation;
            this.initializeStructureData(manager);
        }

        public Piece(StructureManager manager, CompoundTag tag) {
            super(NatureStructures.END_CRYPT_PIECE, tag);
            this.template = new Identifier(tag.getString("Template"));
            this.rotation = BlockRotation.valueOf(tag.getString("Rot"));
            this.initializeStructureData(manager);
        }

        private void initializeStructureData(StructureManager manager) {
            Structure structure = manager.getStructureOrBlank(this.template);
            StructurePlacementData structurePlacementData = (new StructurePlacementData()).setRotation(this.rotation).setMirror(BlockMirror.NONE).setPosition((BlockPos)EndCryptPieces.OFFSET.get(this.template)).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
            this.setStructureData(structure, this.pos, structurePlacementData);
        }

        protected void toNbt(CompoundTag tag) {
            super.toNbt(tag);
            tag.putString("Template", this.template.toString());
            tag.putString("Rot", this.rotation.name());
        }

        protected void handleMetadata(String metadata, BlockPos pos, WorldAccess world, Random random, BlockBox boundingBox) {
            if ("Chest".equals(metadata)) {
                BlockEntity blockpos = world.getBlockEntity(pos.up());
                if (blockpos instanceof ChestBlockEntity) {
                    ((ChestBlockEntity)blockpos).setLootTable(LootTables.END_CITY_TREASURE_CHEST, random.nextLong());
                }
            }
        }


        public boolean generate(ServerWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            StructurePlacementData structurePlacementData = (new StructurePlacementData()).setRotation(this.rotation).setMirror(BlockMirror.NONE).setPosition((BlockPos)EndCryptPieces.OFFSET.get(this.template)).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
            BlockPos blockPos2 = (BlockPos)EndCryptPieces.OFFSET.get(this.template);
            BlockPos blockPos3 = this.pos.add(Structure.transform(structurePlacementData, new BlockPos(3 - blockPos2.getX(), 0, 0 - blockPos2.getZ())));
            int i = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, blockPos3.getX(), blockPos3.getZ());
            BlockPos blockPos4 = this.pos;
            this.pos = this.pos.add(0, i - 90 - 1, 0);
            boolean bl = super.generate(world, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, blockPos);

            this.pos = blockPos4;
            return bl;
        }
    }

}
