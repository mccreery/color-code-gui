package nukeduck.ccgui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public final class Constants {
	private Constants() {}

	public static final String MODID = "ccgui";
	public static final Minecraft MINECRAFT = Minecraft.getMinecraft();

	public static final ResourceLocation ICONS        = new ResourceLocation("ccgui", "textures/gui/icons.png");
	//public static final ResourceLocation BUTTON_PRESS = new ResourceLocation("gui.button.press");

	public static final int SPACING     = 5;
	public static final int FONT_HEIGHT = 9;
	public static final int SCROLLBAR   = 0x55000000;
	public static final int TRANSPARENT = 0x55000000;
	public static final int TEXT        = 0xFFFFFFFF;

	public static final String LANG_UNICODE       = MODID + ".unicode";
	public static final String LANG_UNICODE_TITLE = MODID + ".unicodeTitle";
	public static final String LANG_RAINBOW       = MODID + ".rainbow";
	public static final String LANG_TABLE         = MODID + ".table";
}
