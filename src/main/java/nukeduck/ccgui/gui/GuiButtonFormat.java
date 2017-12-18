package nukeduck.ccgui.gui;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import nukeduck.ccgui.util.Constants;
import nukeduck.ccgui.util.GraphicsUtils;
import nukeduck.ccgui.util.Utils;

public class GuiButtonFormat extends GuiButtonTooltip {
	public final ChatFormatting format;

	public GuiButtonFormat(int buttonId, int x, int y, ChatFormatting format) {
		super(buttonId, x, y, 20, 20, "");
		this.format = format;

		Character display = getDisplayLetter(format);
		if(display != null) {
			this.displayString = this.format.toString() + display;
		}
		this.tooltip.add(Utils.getDisplayName(this.format.getName(), '_'));
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		super.drawButton(mc, mouseX, mouseY);

		if(this.format.isColor()) {
			GraphicsUtils.drawButtonImage(
				Constants.ICONS, this, 0xFFFFFF, 0, 0, 16, 16);

			GraphicsUtils.drawButtonImage(Constants.ICONS, this,
				mc.fontRendererObj.getColorCode(this.format.getChar()),
				16, 0, 16, 16);
		}
	}

	/** @return The appropriate character corresponding to a format */
	private static final Character getDisplayLetter(ChatFormatting format) {
		switch(format) {
			case OBFUSCATED:    return 'O';
			case BOLD:          return 'B';
			case STRIKETHROUGH: return 'S';
			case UNDERLINE:     return 'U';
			case ITALIC:        return 'I';
			case RESET:         return 'R';
			default:            return null;
		}
	}
}
