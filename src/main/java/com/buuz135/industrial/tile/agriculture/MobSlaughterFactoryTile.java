package com.buuz135.industrial.tile.agriculture;

import com.buuz135.industrial.proxy.CommonProxy;
import com.buuz135.industrial.proxy.FluidsRegistry;
import com.buuz135.industrial.tile.WorkingAreaElectricMachine;
import com.buuz135.industrial.tile.block.MobSlaughterFactoryBlock;
import com.buuz135.industrial.utils.ItemStackUtils;
import com.buuz135.industrial.utils.WorkUtils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.FluidTankType;

import java.util.List;

public class MobSlaughterFactoryTile extends WorkingAreaElectricMachine {

    private IFluidTank outMeat;
    private IFluidTank outPink;

    public MobSlaughterFactoryTile() {
        super(MobSlaughterFactoryTile.class.getName().hashCode(), 2, 1, false);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();
        outMeat = this.addFluidTank(FluidsRegistry.MEAT, 8000, EnumDyeColor.BROWN, "Meat tank", new BoundingRectangle(46, 25, 18, 54));
        outPink = this.addSimpleFluidTank(8000, "Pink Slime Tank", EnumDyeColor.PINK, 46 + 20, 25, FluidTankType.OUTPUT, fluidStack -> false, fluidStack -> true);
    }

    @Override
    public AxisAlignedBB getWorkingArea() {
        EnumFacing f = this.getFacing().getOpposite();
        BlockPos corner1 = new BlockPos(0, 0, 0).offset(f, getRadius() + 1).offset(EnumFacing.UP, getHeight());
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).grow(getRadius(), getHeight(), getRadius()).offset(corner1);
    }

    @Override
    public float work() {
        if (WorkUtils.isDisabled(this.getBlockType())) return 0;

        AxisAlignedBB area = getWorkingArea();
        List<EntityLiving> mobs = this.getWorld().getEntitiesWithinAABB(EntityLiving.class, area);
        if (mobs.size() == 0) return 0;
        EntityLiving mob = mobs.get(this.getWorld().rand.nextInt(mobs.size()));
        this.outMeat.fill(new FluidStack(FluidsRegistry.MEAT, (int) (mob.getHealth() * ((MobSlaughterFactoryBlock) this.getBlockType()).getMeatValue())), true);
        this.outPink.fill(new FluidStack(FluidsRegistry.PINK_SLIME, (int) mob.getHealth()), true);
        mob.setDropItemsWhenDead(false);
        mob.attackEntityFrom(CommonProxy.custom, mob.getMaxHealth());

        return 1;
    }

    @Override
    protected boolean acceptsFluidItem(ItemStack stack) {
        return ItemStackUtils.acceptsFluidItem(stack);
    }

    @Override
    protected void processFluidItems(ItemStackHandler fluidItems) {
        ItemStackUtils.processFluidItems(fluidItems, outMeat);
    }
}
