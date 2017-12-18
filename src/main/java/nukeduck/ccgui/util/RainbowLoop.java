package nukeduck.ccgui.util;

import java.util.Iterator;

import com.mojang.realmsclient.gui.ChatFormatting;

public final class RainbowLoop implements Iterable<ChatFormatting> {
	/** The single instance. Use this instead of constructing. */
	public static final RainbowLoop INSTANCE = new RainbowLoop();
	private RainbowLoop() {}

	/** The order of the rainbow */
	private static final ChatFormatting[] loop = {
		ChatFormatting.DARK_RED,    ChatFormatting.RED,
		ChatFormatting.GOLD,        ChatFormatting.YELLOW,
		ChatFormatting.GREEN,       ChatFormatting.DARK_GREEN,
		ChatFormatting.AQUA,        ChatFormatting.DARK_AQUA,
		ChatFormatting.BLUE,        ChatFormatting.DARK_BLUE,
		ChatFormatting.DARK_PURPLE, ChatFormatting.LIGHT_PURPLE
	};

	@Override
	public Iterator<ChatFormatting> iterator() {
		return this.new IteratorRainbow();
	}

	private class IteratorRainbow implements Iterator<ChatFormatting> {
		/** The current item's index in the loop */
		private int loopIndex = 0;

		@Override
		public boolean hasNext() {return true;}

		/** @return The next in a repeating sequence of
		 * rainbow formatting codes
		 * @see #loopIndex */
		@Override
		public ChatFormatting next() {
			if(this.loopIndex >= loop.length) loopIndex = 0;
			return loop[this.loopIndex++];
		}
	}
}
