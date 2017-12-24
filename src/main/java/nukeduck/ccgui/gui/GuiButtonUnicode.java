package nukeduck.ccgui.gui;

import net.minecraft.client.resources.I18n;

public class GuiButtonUnicode extends GuiButtonTooltip {
	public GuiButtonUnicode(int buttonId, int x, int y) {
		super(buttonId, x, y, 108, 20, I18n.format("ccgui.unicode"));
		this.tooltip.add(I18n.format("ccgui.unicodeTitle"));
	}

	public boolean toggled = false;

	public boolean toggle() {
		return toggled = !toggled;
	}

	@Override
	protected int getHoverState(boolean mouseOver) {
		return enabled && toggled ? 2 : super.getHoverState(mouseOver);
	}
}
