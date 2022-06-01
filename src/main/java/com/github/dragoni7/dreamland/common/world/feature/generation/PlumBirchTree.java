package com.github.dragoni7.dreamland.common.world.feature.generation;

import java.util.Random;

import com.github.dragoni7.dreamland.common.world.feature.util.FeatureBuilder;
import com.github.dragoni7.dreamland.core.registry.DreamlandBlocks;
import com.github.dragoni7.dreamland.core.registry.DreamlandWoodSets;
import com.github.dragoni7.dreamland.util.RollBoolean;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class PlumBirchTree extends Feature<NoneFeatureConfiguration> {
	
	private static final int MAX_TRUNK_HEIGHT = 16;
	private static final int MIN_TRUNK_HEIGHT = 9;

	public PlumBirchTree(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel worldgenlevel = context.level();
		BlockPos blockpos = context.origin();
		Random rand = context.random();
		FeatureBuilder plumBirchTreeBuilder = new FeatureBuilder();
		int height = rand.nextInt(MIN_TRUNK_HEIGHT, MAX_TRUNK_HEIGHT);
		
		if (worldgenlevel.isEmptyBlock(blockpos.below()) || !DreamlandBlocks.PLUM_BIRCH_SAPLING.get().defaultBlockState().canSurvive(worldgenlevel, blockpos)) {
			return false;
		}
		
		if ( !createTrunk(worldgenlevel, plumBirchTreeBuilder, blockpos, height)) {
			return false;
		}
		
		if ( !createBranches(worldgenlevel, rand, blockpos, plumBirchTreeBuilder, height)) {
			return false;
		}
		
		if ( !createLeaves(worldgenlevel, blockpos, plumBirchTreeBuilder, rand, height)) {
			return false;
		}
		
		plumBirchTreeBuilder.build(worldgenlevel);
		return true;
	}
	
	private static boolean createTrunk(WorldGenLevel worldgenlevel, FeatureBuilder builder, BlockPos blockpos, int height) {
		final BlockState log = DreamlandWoodSets.PLUM_BIRCH.getLog().defaultBlockState();
		
		for (int i = 0; i <= height; i++) {
			BlockPos trunkPos = blockpos.above(i);
			
			if (!builder.addInput(worldgenlevel, log, trunkPos)) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean createBranches(WorldGenLevel level, Random rand, BlockPos blockpos, FeatureBuilder builder, int height) {
		final BlockState log = DreamlandWoodSets.PLUM_BIRCH.getLog().defaultBlockState();
		BlockPos branchPos = blockpos.above(height - rand.nextInt(4, 6));
		boolean canBuild = true;
		
		if (RollBoolean.roll(4, rand)) {
			switch (rand.nextInt(3)) {
			case 0: {
				branchPos = branchPos.north();
				canBuild = builder.addInput(level, log.setValue(RotatedPillarBlock.AXIS, Direction.NORTH.getAxis()), branchPos);
				break;
			}
			case 1: {
				branchPos = branchPos.south();
				canBuild = builder.addInput(level, log.setValue(RotatedPillarBlock.AXIS, Direction.SOUTH.getAxis()), branchPos);
				break;
			}
			case 2: {
				branchPos = branchPos.east();
				canBuild = builder.addInput(level, log.setValue(RotatedPillarBlock.AXIS, Direction.EAST.getAxis()), branchPos);
				break;
			}
			case 3: {
				branchPos = branchPos.west();
				canBuild = builder.addInput(level, log.setValue(RotatedPillarBlock.AXIS, Direction.WEST.getAxis()), branchPos);
				break;
			}
			}
			
			if (RollBoolean.roll(4, rand)) {
				canBuild = builder.addInput(level, Blocks.BEE_NEST.defaultBlockState(), branchPos.below());
			}
		}
		
		return canBuild;
	}
	
	private static boolean createLeaves(WorldGenLevel level, BlockPos pos, FeatureBuilder builder, Random rand, int height) {
		final BlockState leaves = DreamlandBlocks.PLUM_BIRCH_LEAVES.get().defaultBlockState().setValue(LeavesBlock.DISTANCE, 1);
		BlockPos leavesPos = pos.above(height);
		boolean canBuild = true;
		
		canBuild = builder.addInput(level, leaves, leavesPos);
		canBuild = builder.addInput(level, leaves, leavesPos.north());
		canBuild = builder.addInput(level, leaves, leavesPos.south());
		canBuild = builder.addInput(level, leaves, leavesPos.east());
		canBuild = builder.addInput(level, leaves, leavesPos.west());
		
		leavesPos = leavesPos.below();
		
		canBuild = builder.addInput(level, leaves, leavesPos.north());
		canBuild = builder.addInput(level, leaves, leavesPos.south());
		canBuild = builder.addInput(level, leaves, leavesPos.east());
		canBuild = builder.addInput(level, leaves, leavesPos.west());
		
		if (RollBoolean.roll(2, rand)) {
			canBuild = builder.addInput(level, leaves, leavesPos.north().east());
		}
		if (RollBoolean.roll(2, rand)) {
			canBuild = builder.addInput(level, leaves, leavesPos.north().west());
		}
		if (RollBoolean.roll(2, rand)) {
			canBuild = builder.addInput(level, leaves, leavesPos.south().east());
		}
		if (RollBoolean.roll(2, rand)) {
			canBuild = builder.addInput(level, leaves, leavesPos.south().west());
		}
		
		for ( int i = 1; i <= 2; i++) {
			for ( int j = 1; j <= 2; j++) {
				canBuild = builder.addInput(level, leaves, leavesPos.below(i).north(j));
				canBuild = builder.addInput(level, leaves, leavesPos.below(i).north(j).east());
				if (RollBoolean.roll(2, rand)) {
					canBuild = builder.addInput(level, leaves, leavesPos.below(i).north(j).east().east());
				}
				canBuild = builder.addInput(level, leaves, leavesPos.below(i).north(j).west());
				if (RollBoolean.roll(2, rand)) {
					canBuild = builder.addInput(level, leaves, leavesPos.below(i).north(j).west().west());
				}
				
				canBuild = builder.addInput(level, leaves, leavesPos.below(i).south(j));
				canBuild = builder.addInput(level, leaves, leavesPos.below(i).south(j).east());
				if (RollBoolean.roll(2, rand)) {
					canBuild = builder.addInput(level, leaves, leavesPos.below(i).south(j).east().east());
				}
				canBuild = builder.addInput(level, leaves, leavesPos.below(i).south(j).west());
				if (RollBoolean.roll(2, rand)) {
					canBuild = builder.addInput(level, leaves, leavesPos.below(i).south(j).west().west());
				}
				
				canBuild = builder.addInput(level, leaves, leavesPos.below(i).east(j));
				canBuild = builder.addInput(level, leaves, leavesPos.below(i).east(j).north());
				if (RollBoolean.roll(2, rand)) {
					canBuild = builder.addInput(level, leaves, leavesPos.below(i).east(j).north().north());
				}
				canBuild = builder.addInput(level, leaves, leavesPos.below(i).east(j).south());
				if (RollBoolean.roll(2, rand)) {
					canBuild = builder.addInput(level, leaves, leavesPos.below(i).east(j).south().south());
				}
				
				canBuild = builder.addInput(level, leaves, leavesPos.below(i).west(j));
				canBuild = builder.addInput(level, leaves, leavesPos.below(i).west(j).north());
				if (RollBoolean.roll(2, rand)) {
					canBuild = builder.addInput(level, leaves, leavesPos.below(i).west(j).north().north());
				}
				canBuild = builder.addInput(level, leaves, leavesPos.below(i).west(j).south());
				if (RollBoolean.roll(2, rand)) {
					canBuild = builder.addInput(level, leaves, leavesPos.below(i).west(j).south().south());
				}
			}
		}
		return true;
	}

}
