package snownee.cuisine.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.kiwi.block.BlockMod;

public class BlockShearedLeaves extends BlockMod implements IShearable
{
    public static final PropertyBool FLOWER = PropertyBool.create("flower");

    public BlockShearedLeaves(String name)
    {
        super(name, Material.LEAVES);
        this.setCreativeTab(Cuisine.CREATIVE_TAB);
        this.setHardness(0.2F);
        this.setLightOpacity(1);
        setDefaultState(blockState.getBaseState().withProperty(FLOWER, false).withProperty(BlockModSapling.VARIANT, BlockModSapling.Type.POMELO));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void mapModel()
    {
        Item item = Item.getItemFromBlock(this);
        BlockModSapling.Type[] values = BlockModSapling.Type.values();
        for (int i = 0; i < getItemSubtypeAmount(); ++i)
        {
            ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(getRegistryName(), "flower=" + (i > 6) + ",variant=" + values[i % 7].getName()));
        }
    }

    @Override
    public int getItemSubtypeAmount()
    {
        return 14;
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        return NonNullList.withSize(1, getItemInternal(world.getBlockState(pos)));
    }

    @Override
    public boolean isLeaves(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FLOWER, BlockModSapling.VARIANT);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        IBlockState state = getDefaultState().withProperty(BlockModSapling.VARIANT, BlockModSapling.Type.values()[meta % 7]);
        if (meta > 6)
        {
            state = state.withProperty(FLOWER, true);
        }
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int meta = state.getValue(BlockModSapling.VARIANT).ordinal();
        if (state.getValue(FLOWER))
        {
            meta += 7;
        }
        return meta;
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return getMetaFromState(state);
    }

    ItemStack getItemInternal(IBlockState state)
    {
        return new ItemStack(this, 1, damageDropped(state));
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        Random rand = world instanceof World ? ((World) world).rand : RANDOM;
        int chance = 20;

        if (fortune > 0)
        {
            chance -= 2 << fortune;
            if (chance < 10)
                chance = 10;
        }

        if (rand.nextInt(chance) == 0)
        {
            drops.add(new ItemStack(CuisineRegistry.SAPLING, 1, getMetaFromState(state) % 7));
        }
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        return true;
    }
}