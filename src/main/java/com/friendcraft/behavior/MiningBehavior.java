package com.friendcraft.behavior;

import com.friendcraft.entity.AIPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Optional;

/**
 * Mining behavior - AI player searches for and mines specified blocks.
 */
public class MiningBehavior extends AIBehavior {
    private BlockPos mineTarget = null;
    private int searchTimer = 0;
    private Block targetBlock = Blocks.STONE;
    
    public MiningBehavior() {
        super(BehaviorState.MINING, "mine");
    }
    
    @Override
    public void execute(AIPlayer aiPlayer) {
        // Search for target block if no target
        if (mineTarget == null) {
            searchTimer++;
            if (searchTimer >= 20) { // Search every second
                searchTimer = 0;
                findTargetBlock(aiPlayer);
            }
            return;
        }
        
        // Move to target block
        double distance = aiPlayer.position().distanceTo(mineTarget.getCenter());
        if (distance > 3.0) {
            // Move towards mine target
            double dx = mineTarget.getX() - aiPlayer.getX();
            double dy = mineTarget.getY() - aiPlayer.getY();
            double dz = mineTarget.getZ() - aiPlayer.getZ();
            
            double speed = 0.5;
            aiPlayer.setDeltaMovement(
                dx / distance * speed,
                dy > 0 ? 0.5 : 0,
                dz / distance * speed
            );
        } else {
            // Mine the block
            BlockState blockState = aiPlayer.level().getBlockState(mineTarget);
            if (!blockState.isAir()) {
                aiPlayer.level().destroyBlock(mineTarget, true);
                mineTarget = null; // Reset target after mining
            }
        }
    }
    
    private void findTargetBlock(AIPlayer aiPlayer) {
        // Search in a 10-block radius around the AI player
        AABB searchArea = aiPlayer.getBoundingBox().inflate(10.0);
        BlockPos.betweenClosedStream(searchArea).forEach(pos -> {
            BlockState state = aiPlayer.level().getBlockState(pos);
            if (state.getBlock() == targetBlock && mineTarget == null) {
                mineTarget = pos.immutable();
            }
        });
    }
    
    public void setTargetBlock(Block block) {
        this.targetBlock = block;
    }
    
    public BlockPos getMineTarget() {
        return mineTarget;
    }
}
