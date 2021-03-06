package nukeduck.ccgui.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import nukeduck.ccgui.ColorCodeGUI;
import nukeduck.ccgui.util.GraphicsUtils;

public class GuiButtonRainbow extends GuiButtonTooltip {
	public GuiButtonRainbow(int buttonId, int x, int y) {
		super(buttonId, x, y, 20, 20, "");
		this.tooltip.add(I18n.format("ccgui.rainbow"));
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		super.drawButton(mc, mouseX, mouseY, partialTicks);
		GraphicsUtils.drawButtonImage(ColorCodeGUI.ICONS, this, 0xFFFFFF,
			32, 0, 16, 16);
	}

	public boolean toggled = false;
	public boolean toggle() {
		return this.toggled = !this.toggled;
	}

	@Override
	protected int getHoverState(boolean mouseOver) {
		if(!this.enabled) return 0;

		if(mouseOver || this.toggled) {
			return 2;
		} else {
			return 1;
		}
	}
}
