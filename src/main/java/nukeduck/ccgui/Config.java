package nukeduck.ccgui;

import static com.mojang.realmsclient.gui.ChatFormatting.PREFIX_CODE;
import static nukeduck.ccgui.ColorCodeGUI.MC;

import java.io.File;
import java.util.regex.Pattern;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.common.config.Configuration;

public class Config extends Configuration {
	private static final Pattern PREFIX = Pattern.compile(".|bukkit|vanilla", Pattern.CASE_INSENSITIVE);

	public static char prefix;
	public static boolean twoLines;
	private static int scale;
	public static boolean top;

	public Config(File file) {
		super(file);
	}

	@Override
	public void load() {
		super.load();

		String prefix = getString("prefix", Configuration.CATEGORY_GENERAL, "&", "[vanilla, bukkit, &, " + PREFIX_CODE + ", any character]", PREFIX);

		if(prefix.length() == 1) {
			Config.prefix = prefix.charAt(0);
		} else if(prefix.equalsIgnoreCase("bukkit")) {
			Config.prefix = '&';
		} else {
			Config.prefix = PREFIX_CODE;
		}

		twoLines = getBoolean("twoLines", Configuration.CATEGORY_GENERAL, true, "Splits the color code buttons into two lines");
		scale = getInt("scale", Configuration.CATEGORY_GENERAL, 0, 0, 4, "0 for default GUI scale or 1-4 for custom");
		top = getBoolean("top", Configuration.CATEGORY_GENERAL, false, "Moves the color code buttons to the top of the screen");

		if(hasChanged()) save();
	}

	/** @return The custom mod-wide GUI scale factor */
	public static final int scale() {
		return scale != 0 ? scale : new ScaledResolution(MC).getScaleFactor();
	}

	/** @return The text version of the formatting code */
	public static final String getCode(ChatFormatting format) {
		return new String(new char[] {prefix, format.getChar()});
	}
}
