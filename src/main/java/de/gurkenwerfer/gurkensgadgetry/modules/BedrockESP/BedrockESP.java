/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package de.gurkenwerfer.gurkensgadgetry.modules.BedrockESP;

import de.gurkenwerfer.gurkensgadgetry.GurkensGadgetry;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.BlockUpdateEvent;
import meteordevelopment.meteorclient.events.world.ChunkDataEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.GenericSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.UnorderedArrayList;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.RainbowColors;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BedrockESP extends Module {

    // TODO: Fix Tracers

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public BedrockESP() {
        super(GurkensGadgetry.CATEGORY, "BedrockESP", "Renders Illegal Bedrock through walls.");
        RainbowColors.register(this::onTickRainbow);
    }

    //private final List<Block> blocks = new ArrayList<>(List.of(Blocks.BEDROCK));
    private final List<Block> blocks = new LinkedList<>(List.of(Blocks.BEDROCK));

/*
    private final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("blocks")
        .description("Blocks to search for.")
        .defaultValue(Blocks.BEDROCK)
        .onChanged(blocks1 -> {
            if (isActive() && Utils.canUpdate()) onActivate();
        })
        .build()
    );
*/

    private final Setting<ESPBlockData> blockConfigs = sgGeneral.add(new GenericSetting.Builder<ESPBlockData>()
        .name("color-config")
        .description("Configure color of box and tracer.")
        .defaultValue(
            new ESPBlockData(
                ShapeMode.Lines,
                new SettingColor(0, 255, 200),
                new SettingColor(0, 255, 200, 25),
                false,
                new SettingColor(0, 255, 200, 125)
            )
        )
        .build()
    );

    private final Setting<Boolean> tracers = sgGeneral.add(new BoolSetting.Builder()
        .name("tracers")
        .description("Render tracer lines.")
        .defaultValue(false)
        .build()
    );

    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();

    private final Long2ObjectMap<ESPChunkFilter> chunks = new Long2ObjectOpenHashMap<>();
    private final List<ESPGroup> groups = new UnorderedArrayList<>();

    private Dimension lastDimension;

    @Override
    public void onActivate() {
        synchronized (chunks) {
            chunks.clear();
            groups.clear();
        }

        for (Chunk chunk : Utils.chunks()) {
            searchChunk(chunk, null);
        }

        lastDimension = PlayerUtils.getDimension();
    }

    @Override
    public void onDeactivate() {
        synchronized (chunks) {
            chunks.clear();
            groups.clear();
        }
    }

    private void onTickRainbow() {
        if (!isActive()) return;

        ESPBlockData blockData = blockConfigs.get();
        blockData.tickRainbow();
    }

    ESPBlockData getBlockData(Block block) {
        ESPBlockData blockData = blockConfigs.get();
        return blockData == null ? blockConfigs.get() : blockData;
    }

    private void updateChunk(int x, int z) {
        ESPChunkFilter chunk = chunks.get(ChunkPos.toLong(x, z));
        if (chunk != null) chunk.update();
    }

    private void updateBlock(int x, int y, int z) {
        ESPChunkFilter chunk = chunks.get(ChunkPos.toLong(x >> 4, z >> 4));
        if (chunk != null) chunk.update(x, y, z);
    }

    public ESPBlock getBlock(int x, int y, int z) {
        ESPChunkFilter chunk = chunks.get(ChunkPos.toLong(x >> 4, z >> 4));
        return chunk == null ? null : chunk.get(x, y, z);
    }

    public ESPGroup newGroup(Block block) {
        synchronized (chunks) {
            ESPGroup group = new ESPGroup(block);
            groups.add(group);
            return group;
        }
    }

    public void removeGroup(ESPGroup group) {
        synchronized (chunks) {
            groups.remove(group);
        }
    }

    /*
    @EventHandler
    private void onChunkData(ChunkDataEvent event) {
        searchChunk(event.chunk, event);
    }

     */

    private void searchChunk(Chunk chunk, ChunkDataEvent event) {
        MeteorExecutor.execute(() -> {
            if (!isActive()) return;
            ESPChunkFilter schunk = ESPChunkFilter.searchChunk(chunk, blocks);

            if (schunk.size() > 0) {
                synchronized (chunks) {
                    chunks.put(chunk.getPos().toLong(), schunk);
                    schunk.update();

                    // Update neighbour chunks
                    updateChunk(chunk.getPos().x - 1, chunk.getPos().z);
                    updateChunk(chunk.getPos().x + 1, chunk.getPos().z);
                    updateChunk(chunk.getPos().x, chunk.getPos().z - 1);
                    updateChunk(chunk.getPos().x, chunk.getPos().z + 1);
                }
            }

            //if (event != null) ChunkDataEvent.returnChunkDataEvent(event);
        });
    }

    @EventHandler
    private void onBlockUpdate(BlockUpdateEvent event) {
        // Minecraft probably reuses the event.pos BlockPos instance because it causes problems when trying to use it inside another thread
        int bx = event.pos.getX();
        int by = event.pos.getY();
        int bz = event.pos.getZ();

        int chunkX = bx >> 4;
        int chunkZ = bz >> 4;
        long key = ChunkPos.toLong(chunkX, chunkZ);

        boolean added = blocks.contains(event.newState.getBlock()) && !blocks.contains(event.oldState.getBlock());
        boolean removed = !added && !blocks.contains(event.newState.getBlock()) && blocks.contains(event.oldState.getBlock());

        if (added || removed) {
            MeteorExecutor.execute(() -> {
                synchronized (chunks) {
                    ESPChunkFilter chunk = chunks.get(key);

                    if (chunk == null) {
                        chunk = new ESPChunkFilter(chunkX, chunkZ);
                        if (chunk.shouldBeDeleted()) return;

                        chunks.put(key, chunk);
                    }

                    blockPos.set(bx, by, bz);

                    if (added) chunk.add(blockPos);
                    else chunk.remove(blockPos);

                    // Update neighbour blocks
                    for (int x = -1; x < 2; x++) {
                        for (int z = -1; z < 2; z++) {
                            for (int y = -1; y < 2; y++) {
                                if (x == 0 && y == 0 && z == 0) continue;

                                updateBlock(bx + x, by + y, bz + z);
                            }
                        }
                    }
                }
            });
        }
    }

    @EventHandler
    private void onPostTick(TickEvent.Post event) {
        Dimension dimension = PlayerUtils.getDimension();

        if (lastDimension != dimension) onActivate();

        lastDimension = dimension;
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        synchronized (chunks) {
            for (Iterator<ESPChunkFilter> it = chunks.values().iterator(); it.hasNext();) {
                ESPChunkFilter chunk = it.next();

                if (chunk.shouldBeDeleted()) {
                    MeteorExecutor.execute(() -> {
                        for (ESPBlock block : chunk.blocks.values()) {
                            block.group.remove(block, false);
                            block.loaded = false;
                        }
                    });

                    it.remove();
                }
                else chunk.render(event);
            }

            if (tracers.get()) {
                for (Iterator<ESPGroup> it = groups.iterator(); it.hasNext();) {
                    ESPGroup group = it.next();

                    if (group.blocks.isEmpty()) it.remove();
                    else group.render(event);
                }
            }
        }
    }
}
