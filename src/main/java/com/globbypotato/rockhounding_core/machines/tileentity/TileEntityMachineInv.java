package com.globbypotato.rockhounding_core.machines.tileentity;

import com.globbypotato.rockhounding_core.machines.tileentity.WrappedItemHandler.WriteMode;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public abstract class TileEntityMachineInv extends TileEntityMachineBase implements ITickable{

	public final int INPUT_SLOTS;
	public final int OUTPUT_SLOTS;
	public int SIZE;
	protected MachineStackHandler input;
	protected IItemHandlerModifiable automationInput;
	protected MachineStackHandler output;
	protected IItemHandlerModifiable automationOutput;

	public static final int INPUT_SLOT = 0;
	public static final int OUTPUT_SLOT = 0;
	public static final int CONSUMABLE_SLOT = 2;

	public abstract int getGUIHeight();
	
	public TileEntityMachineInv(int inputSlots, int outputSlots){
		this.INPUT_SLOTS = inputSlots;
		this.OUTPUT_SLOTS = outputSlots;
		this.SIZE = INPUT_SLOTS + OUTPUT_SLOTS;

		input = new MachineStackHandler(inputSlots,this){
			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
				return super.insertItem(slot, stack, simulate);
			}
		};
		automationInput = new WrappedItemHandler(input, WriteMode.IN_OUT);

		output = new MachineStackHandler(outputSlots,this){
			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
				return stack;
			}
			@Override
		    protected void onContentsChanged(int slot){
				this.tile.markDirty();
		    }
		};
		automationOutput = new WrappedItemHandler(output, WriteMode.IN_OUT){
			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate){
				ItemStack stack = getStackInSlot(slot);
				if(stack!= null) return super.extractItem(slot, amount, simulate);
				else return null;
			}
		};

	}

	public boolean isCooking() {
		return false;
	}

	public IItemHandler getOutput() {
		return this.output;
	}

	public IItemHandler getInput() {
		return this.input;
	}

	public IItemHandler getInventory(){
		return new CombinedInvWrapper(input, output);
	}

	public IItemHandler getCombinedHandler(){
		return new CombinedInvWrapper(new IItemHandlerModifiable[]{automationInput, automationOutput});
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		input.deserializeNBT(compound.getCompoundTag("input"));
		output.deserializeNBT(compound.getCompoundTag("output"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("input", input.serializeNBT());
		compound.setTag("output", output.serializeNBT());
		return compound;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getCombinedHandler());
		}
		return super.getCapability(capability, facing);
	}
}