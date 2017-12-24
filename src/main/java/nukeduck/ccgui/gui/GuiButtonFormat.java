package nukeduck.ccgui.gui;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import nukeduck.ccgui.ColorCodeGUI;
import nukeduck.ccgui.util.GraphicsUtils;
import nukeduck.ccgui.util.Util;

public class GuiButtonFormat extends GuiButtonTooltip {
	public final ChatFormatting format;

	public GuiButtonFormat(int buttonId, int x, int y, ChatFormatting format) {
		super(buttonId, x, y, 20, 20, "");
		this.format = format;

		Character display = getDisplayLetter(format);
		if(display != null) {
			this.displayString = this.format.toString() + display;
		}
		this.tooltip.add(Util.toTitle(this.format.getName()));
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		super.drawButton(mc, mouseX, mouseY, partialTicks);

		if(this.format.isColor()) {
			GraphicsUtils.drawButtonImage(
				ColorCodeGUI.ICONS, this, 0xFFFFFF, 0, 0, 16, 16);

			GraphicsUtils.drawButtonImage(ColorCodeGUI.ICONS, this,
				mc.fontRenderer.getColorCode(this.format.getChar()),
				16, 0, 16, 16);
		}
	}

	/** @return The appropriate character corresponding to a format */
	private static char getDisplayLetter(ChatFormatting format) {
		switch(format) {
			case OBFUSCATED:    return 'O';
			case BOLD:          return 'B';
			case STRIKETHROUGH: return 'S';
			case UNDERLINE:     return 'U';
			case ITALIC:        return 'I';
			case RESET:         return 'R';
			default:            return '?';
		}
	}
}
