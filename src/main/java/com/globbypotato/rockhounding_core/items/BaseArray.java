package com.globbypotato.rockhounding_core.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BaseArray extends BaseItem {
	private String[] itemArray;

	public BaseArray(String name, String[] array) {
		super(name);
		setHasSubtypes(true);
		this.itemArray = array;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int i = stack.getItemDamage();
		if( i < 0 || i >= this.itemArray.length){ i = 0; }
		return super.getUnlocalizedName() + "." + this.itemArray[i];
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems){
		for(int i = 0; i < this.itemArray.length; i++){subItems.add(new ItemStack(itemIn, 1, i));}
	}

}