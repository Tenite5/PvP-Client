package pvp.client.client.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import pvp.client.client.core.HudModule;
import pvp.client.client.core.setting.EnumSetting;

public class ArmorOverlay extends HudModule {
	private static final EquipmentSlot[] SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
	private final EnumSetting orientation = this.addSetting(new EnumSetting("orientation", "Orientation", 0, "Horizontal", "Vertical"));
	private final EnumSetting durabilityText = this.addSetting(new EnumSetting("durabilityText", "Durability Text", 0, "Percent", "Amount", "Off"));

	public ArmorOverlay() {
		super("armor", "Armor Status", "Shows armor durability in 4 extra slots.", 0.63, 0.93);
	}

	private boolean horizontal() {
		return this.orientation.is("Horizontal");
	}

	private String durabilityLabel(ItemStack stack) {
		if (stack.isEmpty() || !stack.isDamageableItem() || this.durabilityText.is("Off")) {
			return "";
		}
		int remaining = stack.getMaxDamage() - stack.getDamageValue();
		if (this.durabilityText.is("Percent")) {
			return (int) (remaining * 100.0F / stack.getMaxDamage()) + "%";
		}
		return Integer.toString(remaining);
	}

	private int slotSize() {
		return 20;
	}

	@Override
	public int contentWidth(Minecraft minecraft) {
		return this.horizontal() ? this.slotSize() * 4 + 2 : this.slotSize() + 18;
	}

	@Override
	public int contentHeight(Minecraft minecraft) {
		return this.horizontal() ? this.slotSize() + 10 : this.slotSize() * 4 + 2;
	}

	@Override
	protected void renderContent(GuiGraphicsExtractor graphics, Minecraft minecraft) {
		if (minecraft.player == null) {
			return;
		}
		int x = 2;
		int y = 2;
		for (EquipmentSlot slot : SLOTS) {
			ItemStack stack = minecraft.player.getItemBySlot(slot);
			if (this.background.get()) {
				graphics.fill(x - 1, y - 1, x + 17, y + 17, 0x50000000);
			}
			if (!stack.isEmpty()) {
				graphics.item(stack, x, y);
				graphics.itemDecorations(minecraft.font, stack, x, y);
			}
			String label = this.durabilityLabel(stack);
			if (!label.isEmpty()) {
				graphics.pose().pushMatrix();
				graphics.pose().translate(x, y);
				graphics.pose().scale(0.5F, 0.5F);
				this.drawText(graphics, minecraft, label, this.horizontal() ? 1 : 38, this.horizontal() ? 38 : 12);
				graphics.pose().popMatrix();
			}
			if (this.horizontal()) {
				x += this.slotSize();
			} else {
				y += this.slotSize();
			}
		}
	}
}
