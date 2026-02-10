package com.oldschoolminecraft.OSMEss.Handlers;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class HerobrineTotemHandler {

    public OSMEss plugin;
    public HerobrineTotemHandler(OSMEss plugin) {
        this.plugin = plugin;
    }

    //Layer 0 (Co-Level with set 'block')
    public Location getLocAtLayer0Block1Of8(Block block) {return new Location(block.getWorld(), block.getX() +1, block.getY(), block.getZ() +1);}
    public Location getLocAtLayer0Block2Of8(Block block) {return new Location(block.getWorld(), block.getX(), block.getY(), block.getZ() +1);}
    public Location getLocAtLayer0Block3Of8(Block block) {return new Location(block.getWorld(), block.getX() -1, block.getY(), block.getZ() +1);}
    public Location getLocAtLayer0Block4Of8(Block block) {return new Location(block.getWorld(), block.getX() +1, block.getY(), block.getZ());}
    public Location getLocAtLayer0Block5Of8(Block block) {return new Location(block.getWorld(), block.getX() -1, block.getY(), block.getZ());}
    public Location getLocAtLayer0Block6Of8(Block block) {return new Location(block.getWorld(), block.getX() +1, block.getY(), block.getZ() -1);}
    public Location getLocAtLayer0Block7Of8(Block block) {return new Location(block.getWorld(), block.getX(), block.getY(), block.getZ() -1);}
    public Location getLocAtLayer0Block8Of8(Block block) {return new Location(block.getWorld(), block.getX() -1, block.getY(), block.getZ() -1);}

    public Block getBlockAtLayer0Block1Of8(Block block) {return getLocAtLayer0Block1Of8(block).getBlock();}
    public Block getBlockAtLayer0Block2Of8(Block block) {return getLocAtLayer0Block2Of8(block).getBlock();}
    public Block getBlockAtLayer0Block3Of8(Block block) {return getLocAtLayer0Block3Of8(block).getBlock();}
    public Block getBlockAtLayer0Block4Of8(Block block) {return getLocAtLayer0Block4Of8(block).getBlock();}
    public Block getBlockAtLayer0Block5Of8(Block block) {return getLocAtLayer0Block5Of8(block).getBlock();}
    public Block getBlockAtLayer0Block6Of8(Block block) {return getLocAtLayer0Block6Of8(block).getBlock();}
    public Block getBlockAtLayer0Block7Of8(Block block) {return getLocAtLayer0Block7Of8(block).getBlock();}
    public Block getBlockAtLayer0Block8Of8(Block block) {return getLocAtLayer0Block8Of8(block).getBlock();}

    public boolean meetsTotemCriteriaLayer0(Block block) {
        if (getBlockAtLayer0Block1Of8(block).getType() == Material.AIR &&
                getBlockAtLayer0Block2Of8(block).getType() == Material.AIR &&
                getBlockAtLayer0Block3Of8(block).getType() == Material.AIR &&
                getBlockAtLayer0Block4Of8(block).getType() == Material.AIR &&
                getBlockAtLayer0Block5Of8(block).getType() == Material.AIR &&
                getBlockAtLayer0Block6Of8(block).getType() == Material.AIR &&
                getBlockAtLayer0Block7Of8(block).getType() == Material.AIR &&
                getBlockAtLayer0Block8Of8(block).getType() == Material.AIR) {
            return true;
        }
        else return false;
    }

    // Layer -1 (Below height of set 'block')
    public Location getLocAtLayerNegative1Block1Of9(Block block) {return new Location(block.getWorld(), block.getX() +1, block.getY() -1, block.getZ() +1);}
    public Location getLocAtLayerNegative1Block2Of9(Block block) {return new Location(block.getWorld(), block.getX(), block.getY() -1, block.getZ() +1);}
    public Location getLocAtLayerNegative1Block3Of9(Block block) {return new Location(block.getWorld(), block.getX() -1, block.getY() -1, block.getZ() +1);}
    public Location getLocAtLayerNegative1Block4Of9(Block block) {return new Location(block.getWorld(), block.getX() +1, block.getY() -1, block.getZ());}
    public Location getLocAtLayerNegative1Block5Of9(Block block) {return new Location(block.getWorld(), block.getX(), block.getY() -1, block.getZ());}
    public Location getLocAtLayerNegative1Block6Of9(Block block) {return new Location(block.getWorld(), block.getX() -1, block.getY() -1, block.getZ());}
    public Location getLocAtLayerNegative1Block7Of9(Block block) {return new Location(block.getWorld(), block.getX() +1, block.getY() -1, block.getZ() -1);}
    public Location getLocAtLayerNegative1Block8Of9(Block block) {return new Location(block.getWorld(), block.getX(), block.getY() -1, block.getZ() -1);}
    public Location getLocAtLayerNegative1Block9Of9(Block block) {return new Location(block.getWorld(), block.getX() -1, block.getY() -1, block.getZ() -1);}

    public Block getBlockAtLayerNegative1Block1Of9(Block block) {return getLocAtLayerNegative1Block1Of9(block).getBlock();}
    public Block getBlockAtLayerNegative1Block2Of9(Block block) {return getLocAtLayerNegative1Block2Of9(block).getBlock();}
    public Block getBlockAtLayerNegative1Block3Of9(Block block) {return getLocAtLayerNegative1Block3Of9(block).getBlock();}
    public Block getBlockAtLayerNegative1Block4Of9(Block block) {return getLocAtLayerNegative1Block4Of9(block).getBlock();}
    public Block getBlockAtLayerNegative1Block5Of9(Block block) {return getLocAtLayerNegative1Block5Of9(block).getBlock();}
    public Block getBlockAtLayerNegative1Block6Of9(Block block) {return getLocAtLayerNegative1Block6Of9(block).getBlock();}
    public Block getBlockAtLayerNegative1Block7Of9(Block block) {return getLocAtLayerNegative1Block7Of9(block).getBlock();}
    public Block getBlockAtLayerNegative1Block8Of9(Block block) {return getLocAtLayerNegative1Block8Of9(block).getBlock();}
    public Block getBlockAtLayerNegative1Block9Of9(Block block) {return getLocAtLayerNegative1Block9Of9(block).getBlock();}

    public boolean meetsTotemCriteriaLayerNeg1(Block block) {
        if (getBlockAtLayerNegative1Block1Of9(block).getType() == Material.AIR &&
                getBlockAtLayerNegative1Block2Of9(block).getType() == Material.REDSTONE_TORCH_ON &&
                getBlockAtLayerNegative1Block3Of9(block).getType() == Material.AIR &&
                getBlockAtLayerNegative1Block4Of9(block).getType() == Material.REDSTONE_TORCH_ON &&
                getBlockAtLayerNegative1Block5Of9(block).getType() == Material.NETHERRACK &&
                getBlockAtLayerNegative1Block6Of9(block).getType() == Material.REDSTONE_TORCH_ON &&
                getBlockAtLayerNegative1Block7Of9(block).getType() == Material.AIR &&
                getBlockAtLayerNegative1Block8Of9(block).getType() == Material.REDSTONE_TORCH_ON &&
                getBlockAtLayerNegative1Block9Of9(block).getType() == Material.AIR) {
            return true;
        }
        else return false;
    }

    public Location getLocAtLayerNegative2Block1Of9(Block block) {return new Location(block.getWorld(), block.getX() +1, block.getY() -2, block.getZ() +1);}
    public Location getLocAtLayerNegative2Block2Of9(Block block) {return new Location(block.getWorld(), block.getX(), block.getY() -2, block.getZ() +1);}
    public Location getLocAtLayerNegative2Block3Of9(Block block) {return new Location(block.getWorld(), block.getX() -1, block.getY() -2, block.getZ() +1);}
    public Location getLocAtLayerNegative2Block4Of9(Block block) {return new Location(block.getWorld(), block.getX() +1, block.getY() -2, block.getZ());}
    public Location getLocAtLayerNegative2Block5Of9(Block block) {return new Location(block.getWorld(), block.getX(), block.getY() -2, block.getZ());}
    public Location getLocAtLayerNegative2Block6Of9(Block block) {return new Location(block.getWorld(), block.getX() -1, block.getY() -2, block.getZ());}
    public Location getLocAtLayerNegative2Block7Of9(Block block) {return new Location(block.getWorld(), block.getX() +1, block.getY() -2, block.getZ() -1);}
    public Location getLocAtLayerNegative2Block8Of9(Block block) {return new Location(block.getWorld(), block.getX(), block.getY() -2, block.getZ() -1);}
    public Location getLocAtLayerNegative2Block9Of9(Block block) {return new Location(block.getWorld(), block.getX() -1, block.getY() -2, block.getZ() -1);}

    public Block getBlockAtLayerNegative2Block1Of9(Block block) {return getLocAtLayerNegative2Block1Of9(block).getBlock();}
    public Block getBlockAtLayerNegative2Block2Of9(Block block) {return getLocAtLayerNegative2Block2Of9(block).getBlock();}
    public Block getBlockAtLayerNegative2Block3Of9(Block block) {return getLocAtLayerNegative2Block3Of9(block).getBlock();}
    public Block getBlockAtLayerNegative2Block4Of9(Block block) {return getLocAtLayerNegative2Block4Of9(block).getBlock();}
    public Block getBlockAtLayerNegative2Block5Of9(Block block) {return getLocAtLayerNegative2Block5Of9(block).getBlock();}
    public Block getBlockAtLayerNegative2Block6Of9(Block block) {return getLocAtLayerNegative2Block6Of9(block).getBlock();}
    public Block getBlockAtLayerNegative2Block7Of9(Block block) {return getLocAtLayerNegative2Block7Of9(block).getBlock();}
    public Block getBlockAtLayerNegative2Block8Of9(Block block) {return getLocAtLayerNegative2Block8Of9(block).getBlock();}
    public Block getBlockAtLayerNegative2Block9Of9(Block block) {return getLocAtLayerNegative2Block9Of9(block).getBlock();}

    public boolean meetsTotemCriteriaLayerNeg2(Block block) {
        if (getBlockAtLayerNegative2Block1Of9(block).getType() == Material.GOLD_BLOCK &&
                getBlockAtLayerNegative2Block2Of9(block).getType() == Material.GOLD_BLOCK &&
                getBlockAtLayerNegative2Block3Of9(block).getType() == Material.GOLD_BLOCK &&
                getBlockAtLayerNegative2Block4Of9(block).getType() == Material.GOLD_BLOCK &&
                getBlockAtLayerNegative2Block5Of9(block).getType() == Material.MOSSY_COBBLESTONE &&
                getBlockAtLayerNegative2Block6Of9(block).getType() == Material.GOLD_BLOCK &&
                getBlockAtLayerNegative2Block7Of9(block).getType() == Material.GOLD_BLOCK &&
                getBlockAtLayerNegative2Block8Of9(block).getType() == Material.GOLD_BLOCK &&
                getBlockAtLayerNegative2Block9Of9(block).getType() == Material.GOLD_BLOCK) {
            return true;
        }
        else return false;
    }
}
