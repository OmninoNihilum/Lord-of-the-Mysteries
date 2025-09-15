package net.swimmingtuna.lotm.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.swimmingtuna.lotm.LOTM;

import java.util.LinkedList;
import java.util.Queue;

class Pair<F, S>{
    public F first;
    public S second;

    Pair(F _first, S _second){
        first = _first;
        second = _second;
    }
}

public class DestructionQueue {
    static private Queue<Pair<Level, BlockPos>> queue = new LinkedList<>();
    static final private int maxPerTick = 10000;

    public static void mark(Level level, BlockPos pos){
        queue.add(new Pair<>(level, new BlockPos(pos)));
    }

    public static void destroy(){
        if(queue.isEmpty()) return;

        for(int i = 0; i < maxPerTick; i++) {
            var buff = queue.poll();
            if (buff == null) break;

            buff.first.setBlock(buff.second, Blocks.AIR.defaultBlockState(), 2);
        }
    }
}