package nukeduck.ccgui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public final class GraphicsUtils {
	private GraphicsUtils() {}

	public static void renderBox(int x, int y, int width, int height, int color) {
		Gui.drawRect(x + 1, y, x + width - 1, y + height, color);
		Gui.drawRect(x, y + 1, x + 1, y + height - 1, color);
		Gui.drawRect(x + width - 1, y + 1, x + width, y + height - 1, color);
		
		Gui.drawRect(x + 1, y + 1, x + (width - 1), y + 2, color);
		Gui.drawRect(x + 1, y + height - 2, x + (width - 1), y + height - 1, color);
		
		Gui.drawRect(x + 1, y + 2, x + 2, y + height - 2, color);
		Gui.drawRect(x + (width - 2), y + 2, x + (width - 1), y + height - 2, color);
	}

	public static void drawButtonImage(ResourceLocation texture,
			GuiButton button, int color, int u, int v, int width, int height) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

		GlStateManager.color(
			(float)(color >> 16) / 255.0F, (float)(color >> 8 & 255) / 255.0F,
			(float)(color & 255) / 255.0F, 1.0F);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.blendFunc(770, 771);

		int x = button.x + (button.width - width) / 2;
		int y = button.y + (button.height - height) / 2;
		button.drawTexturedModalRect(x, y, u, v, width, height);
	}
}
