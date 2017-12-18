package nukeduck.ccgui.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiButtonTooltip extends GuiButton {
	/** Default size of a button */
	private static final int BUTTON_WIDTH = 200, BUTTON_HEIGHT = 20;
	public List<String> tooltip;

	public GuiButtonTooltip(int buttonId, int x, int y, String buttonText) {
		this(buttonId, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, buttonText);
	}
	public GuiButtonTooltip(int buttonId, int x, int y,
			String buttonText, List<String> tooltip) {
		this(buttonId, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, buttonText, tooltip);
	}
	public GuiButtonTooltip(int buttonId, int x, int y,
			int width, int height, String buttonText) {
		this(buttonId, x, y, width, height,
			buttonText, new ArrayList<String>());
	}
	public GuiButtonTooltip(int buttonId, int x, int y,
			int width, int height, String buttonText, List<String> tooltip) {
		super(buttonId, x, y, width, height, buttonText);
		this.tooltip = tooltip;
	}

	public boolean drawTooltip(Minecraft mc, int mouseX, int mouseY) {
		if(this.isMouseOver()) {
			GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);

			GuiUtils.drawHoveringText(this.tooltip, mouseX, mouseY,
				mc.displayWidth, mc.displayHeight, -1, mc.fontRenderer);

			GL11.glPopAttrib();
			return true;
		}
		return false;
	}
}
