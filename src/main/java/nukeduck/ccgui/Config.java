package nukeduck.ccgui;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {
	private final Configuration config;

	public final char prefix;
	public final boolean twoLines;
	public final int scale;
	public final boolean top;

	public Config(File configFile) {
		this.config = new Configuration(configFile);
		this.config.load();

		String prefix = this.config.getString("prefix", Configuration.CATEGORY_GENERAL, "vanilla", "\"bukkit\" (&) or \"vanilla\" (\u00A7)");
		this.prefix = prefix.equalsIgnoreCase("bukkit") ? '&' : '\u00A7';

		this.twoLines = this.config.getBoolean("twoLines", Configuration.CATEGORY_GENERAL, true, "Splits the color code buttons into two lines");
		this.scale = this.config.getInt("scale", Configuration.CATEGORY_GENERAL, 0, 0, 4, "0 for default GUI scale or 1-4 for custom");
		this.top = this.config.getBoolean("top", Configuration.CATEGORY_GENERAL, false, "Moves the color code buttons to the top of the screen");

		if(this.config.hasChanged()) this.config.save();
	}
}
