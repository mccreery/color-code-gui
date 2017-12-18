package nukeduck.ccgui.gui;

import net.minecraft.client.resources.I18n;
import nukeduck.ccgui.util.Constants;

public class GuiButtonUnicode extends GuiButtonTooltip {
	public GuiButtonUnicode(int buttonId, int x, int y) {
		super(buttonId, x, y, 108, 20, I18n.format(
			Constants.LANG_UNICODE));
		this.tooltip.add(I18n.format(
			Constants.LANG_UNICODE_TITLE));
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
