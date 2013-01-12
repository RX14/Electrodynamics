package assemblyline.common.machine.imprinter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import assemblyline.common.Pair;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class ContainerImprinter extends Container implements ISlotWatcher
{
	private InventoryPlayer inventoryPlayer;
	private TileEntityImprinter tileEntity;

	public ContainerImprinter(InventoryPlayer inventoryPlayer, TileEntityImprinter tileEntity)
	{
		this.tileEntity = tileEntity;
		this.inventoryPlayer = inventoryPlayer;

		// Paper Input
		this.addSlotToContainer(new SlotImprint(this.tileEntity, 0, 42, 24));
		// Item Stamp
		this.addSlotToContainer(new Slot(this.tileEntity, 1, 78, 24));
		// Output Filter
		this.addSlotToContainer(new SlotImprintResult(this.tileEntity, 2, 136, 24));
		// Crafting Slot
		this.addSlotToContainer(new SlotImprint(this.tileEntity, 3, 78, 53));
		// Crafting Output
		this.addSlotToContainer(new SlotCraftingResult(this, this.tileEntity, 4, 136, 53));

		int var3;

		for (var3 = 0; var3 < 3; ++var3)
		{
			for (int var4 = 0; var4 < 9; ++var4)
			{
				this.addSlotToContainer(new WatchedSlot(inventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18, this));
			}
		}

		for (var3 = 0; var3 < 9; ++var3)
		{
			this.addSlotToContainer(new WatchedSlot(inventoryPlayer, var3, 8 + var3 * 18, 142, this));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return this.tileEntity.isUseableByPlayer(player);
	}

	/**
	 * Called to transfer a stack from one inventory to the other eg. when shift clicking.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot)
	{
		ItemStack copyStack = null;
		Slot slotObj = (Slot) this.inventorySlots.get(slot);

		if (slotObj != null && slotObj.getHasStack())
		{
			ItemStack slotStack = slotObj.getStack();
			copyStack = slotStack.copy();

			if (slot == 2)
			{
				// Prevents filter from being duplicated
				this.tileEntity.setInventorySlotContents(0, null);
			}

			if (slot > 4)
			{
				if (this.getSlot(0).isItemValid(slotStack))
				{
					if (!this.mergeItemStack(slotStack, 0, 1, false)) { return null; }
				}
				else if (!this.mergeItemStack(slotStack, 1, 2, false)) { return null; }
			}
			else if (!this.mergeItemStack(slotStack, this.tileEntity.getSizeInventory(), 37, false)) { return null; }

			if (slotStack.stackSize == 0)
			{
				slotObj.putStack((ItemStack) null);
			}
			else
			{
				slotObj.onSlotChanged();
			}

			if (slotStack.stackSize == copyStack.stackSize) { return null; }

			slotObj.onPickupFromSlot(player, slotStack);
		}

		this.slotContentsChanged();

		return copyStack;
	}

	/**
	 * Does this player's inventory contain the required resources to craft this item?
	 * 
	 * @return Required Items
	 */
	public Pair<ItemStack, ItemStack[]> getIdealRecipe(ItemStack outputItem)
	{
		for (Object object : CraftingManager.getInstance().getRecipeList())
		{
			if (object instanceof IRecipe)
			{
				if (((IRecipe) object).getRecipeOutput() != null)
				{
					if (outputItem.isItemEqual(((IRecipe) object).getRecipeOutput()))
					{
						if (object instanceof ShapedRecipes)
						{
							if (this.hasResource(((ShapedRecipes) object).recipeItems) != null) { return new Pair<ItemStack, ItemStack[]>(((IRecipe) object).getRecipeOutput().copy(), ((ShapedRecipes) object).recipeItems); }
						}
						else if (object instanceof ShapelessRecipes)
						{
							if (this.hasResource(((ShapelessRecipes) object).recipeItems.toArray(new ItemStack[1])) != null) { return new Pair<ItemStack, ItemStack[]>(((IRecipe) object).getRecipeOutput().copy(), (ItemStack[]) ((ShapelessRecipes) object).recipeItems.toArray(new ItemStack[1])); }
						}
						else if (object instanceof ShapedOreRecipe)
						{
							ShapedOreRecipe oreRecipe = (ShapedOreRecipe) object;
							Object[] oreRecipeInput = (Object[]) ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, oreRecipe, "input");

							ArrayList<ItemStack> hasResources = this.hasResource(oreRecipeInput);

							if (hasResources != null) { return new Pair<ItemStack, ItemStack[]>(((IRecipe) object).getRecipeOutput().copy(), hasResources.toArray(new ItemStack[1])); }
						}
						else if (object instanceof ShapelessOreRecipe)
						{
							ShapelessOreRecipe oreRecipe = (ShapelessOreRecipe) object;
							ArrayList oreRecipeInput = (ArrayList) ReflectionHelper.getPrivateValue(ShapelessOreRecipe.class, oreRecipe, "input");

							List<ItemStack> hasResources = this.hasResource(oreRecipeInput.toArray());

							if (hasResources != null) { return new Pair<ItemStack, ItemStack[]>(((IRecipe) object).getRecipeOutput().copy(), hasResources.toArray(new ItemStack[1])); }
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Returns if players has the following resource required.
	 * 
	 * @param recipeItems - The items to be checked for the recipes.
	 */
	private ArrayList<ItemStack> hasResource(Object[] recipeItems)
	{
		/**
		 * The actual amount of resource required. Each ItemStack will only have stacksize of 1.
		 */
		ArrayList<ItemStack> actualResources = new ArrayList<ItemStack>();
		int itemMatch = 0;

		for (Object obj : recipeItems)
		{
			if (obj instanceof ItemStack)
			{
				ItemStack recipeItem = (ItemStack) obj;
				actualResources.add(recipeItem.copy());

				if (recipeItem != null)
				{
					for (int i = 0; i < this.inventoryPlayer.getSizeInventory(); i++)
					{
						ItemStack checkStack = this.inventoryPlayer.getStackInSlot(i);

						if (checkStack != null)
						{
							if (SlotCraftingResult.isItemEqual(recipeItem, checkStack))
							{
								// TODO Do NBT CHecking
								itemMatch++;
								break;
							}
						}
					}
				}
			}
			else if (obj instanceof ArrayList)
			{
				ArrayList ingredientsList = (ArrayList) obj;
				Object[] ingredientsArray = ingredientsList.toArray();

				optionsLoop:
				for (int x = 0; x < ingredientsArray.length; x++)
				{
					if (ingredientsArray[x] != null && ingredientsArray[x] instanceof ItemStack)
					{
						ItemStack recipeItem = (ItemStack) ingredientsArray[x];
						actualResources.add(recipeItem.copy());

						if (recipeItem != null)
						{
							for (int i = 0; i < this.inventoryPlayer.getSizeInventory(); i++)
							{
								ItemStack checkStack = this.inventoryPlayer.getStackInSlot(i);

								if (checkStack != null)
								{
									if (SlotCraftingResult.isItemEqual(recipeItem, checkStack))
									{
										// TODO Do NBT CHecking
										itemMatch++;
										break optionsLoop;
									}
								}
							}
						}
					}
				}
			}
		}

		return itemMatch >= actualResources.size() ? actualResources : null;
	}

	@Override
	public void slotContentsChanged()
	{
		/**
		 * Makes the stamping recipe for filters
		 */
		boolean didStamp = false;

		if (this.tileEntity.getStackInSlot(0) != null && this.tileEntity.getStackInSlot(1) != null)
		{
			if (this.tileEntity.getStackInSlot(0).getItem() instanceof ItemImprinter)
			{
				ItemStack outputStack = this.tileEntity.getStackInSlot(0).copy();
				outputStack.stackSize = 1;
				ArrayList<ItemStack> filters = ItemImprinter.getFilters(outputStack);
				boolean filteringItemExists = false;

				for (ItemStack filteredStack : filters)
				{
					if (filteredStack.isItemEqual(this.tileEntity.getStackInSlot(1)))
					{
						filters.remove(filteredStack);
						filteringItemExists = true;
						break;
					}
				}

				if (!filteringItemExists)
				{
					filters.add(this.tileEntity.getStackInSlot(1));
				}

				ItemImprinter.setFilters(outputStack, filters);
				this.tileEntity.setInventorySlotContents(2, outputStack);
				didStamp = true;
			}
		}

		if (!didStamp)
		{
			this.tileEntity.setInventorySlotContents(2, null);
		}

		// CRAFTING
		boolean didCraft = false;

		if (this.tileEntity.getStackInSlot(3) != null)
		{
			if (this.tileEntity.getStackInSlot(3).getItem() instanceof ItemImprinter)
			{
				ArrayList<ItemStack> filters = ItemImprinter.getFilters(this.tileEntity.getStackInSlot(3));

				if (filters.size() > 0)
				{
					ItemStack outputStack = filters.get(0);

					if (outputStack != null)
					{
						Pair<ItemStack, ItemStack[]> idealRecipe = this.getIdealRecipe(outputStack);

						if (idealRecipe != null)
						{
							this.tileEntity.setInventorySlotContents(4, idealRecipe.getKey());
							didCraft = true;
						}
					}
				}
			}
		}

		if (!didCraft)
		{
			this.tileEntity.setInventorySlotContents(4, null);
		}
	}
}
