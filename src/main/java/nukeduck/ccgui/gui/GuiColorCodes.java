package nukeduck.ccgui.gui;

import java.io.IOException;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import nukeduck.ccgui.ColorCodeGUI;
import nukeduck.ccgui.util.Constants;
import nukeduck.ccgui.util.ITextEditor;
import nukeduck.ccgui.util.RainbowLoop;
import nukeduck.ccgui.util.Utils;

public class GuiColorCodes extends GuiScreen {
	private static final int FORMAT_COUNT = ChatFormatting.values().length;

	private GuiButtonRainbow rainbowToggle;
	private Iterator<ChatFormatting> rainbow;

	private GuiButtonUnicode unicodeToggle;
	private GuiUnicodeTable unicodeTable;

	private final GuiScreen parent;
	private final ITextEditor editor;

	private int prevSelectStart/*, prevSelectEnd, prevLength*/;

	public GuiColorCodes(GuiScreen parent, ITextEditor editor) {
		this.parent = parent;
		this.editor = editor;
	}

	@Override
	public void initGui() {
		ScaledResolution sr = new ScaledResolution(this.mc);
		final float ratio = (float)sr.getScaleFactor() / Utils.getScaleFactor();
		final int minX = (int)(2 * ratio);
		final boolean top = ColorCodeGUI.INSTANCE.config.top
			|| !(this.parent instanceof GuiChat);

		int y = top ? 5 : (int)((sr.getScaledHeight() - 16) * ratio) - 20;
		for(int i = 0, x = minX; i < FORMAT_COUNT + 2; i++, x += 22) {
			if(i < FORMAT_COUNT) {
				if(ColorCodeGUI.INSTANCE.config.twoLines && i == 16) {
					x = minX;
					y += top ? 22 : -22;
				}
				this.buttonList.add(new GuiButtonFormat(i, x, y, ChatFormatting.values()[i]));
			} else if(i == FORMAT_COUNT) {
				this.rainbowToggle = new GuiButtonRainbow(i, x, y);
				this.buttonList.add(this.rainbowToggle);
			} else {
				this.unicodeToggle = new GuiButtonUnicode(i, x, y);
				this.buttonList.add(this.unicodeToggle);
			}
		}
		this.unicodeTable = new GuiUnicodeTable(5, 5, this.editor);

		// TODO pretty bad wouldn't you say
		this.prevSelectStart = this.editor.getSelectStart();
		//this.prevSelectEnd = this.editor.getSelectEnd();
		//this.prevLength = this.editor.length();
	}

	// TODO Improve rainbow mode so it just adds codes when new characters are inserted
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(!this.rainbowToggle.toggled) return;
		int selectStart = this.editor.getSelectStart();

		if(selectStart > this.prevSelectStart) {
			CharSequence added = this.editor.subSequence(this.prevSelectStart, selectStart);
			this.editor.delete(this.prevSelectStart, selectStart);
			this.editor.setCursor(selectStart);

			StringBuilder builder = new StringBuilder(added.length() * 3);
			for(int i = 0; i < added.length(); i++) {
				builder.append(Utils.getFormatString(this.rainbow.next())).append(added.charAt(i));
			}
			this.editor.insertAtCursor(builder.toString());
		}
		this.prevSelectStart = this.editor.getSelectStart();
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(this.unicodeToggle.toggled && this.unicodeTable.mouseClicked(mouseX, mouseY, mouseButton)) {
			return;
		}
		ScaledResolution sr = new ScaledResolution(Constants.MINECRAFT);
		int scale = Utils.getScaleFactor();
		int mouseXS = mouseX * sr.getScaleFactor() / scale;
		int mouseYS = mouseY * sr.getScaleFactor() / scale;

		super.mouseClicked(mouseXS, mouseYS, mouseButton);
	}
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		ScaledResolution sr = new ScaledResolution(Constants.MINECRAFT);
		int scale = Utils.getScaleFactor();
		int mouseXS = mouseX * sr.getScaleFactor() / scale;
		int mouseYS = mouseY * sr.getScaleFactor() / scale;

		super.mouseReleased(mouseXS, mouseYS, state);
		if(this.unicodeToggle.toggled) {
			this.unicodeTable.mouseReleased(mouseX, mouseY, state);
		}
	}
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
		ScaledResolution sr = new ScaledResolution(Constants.MINECRAFT);
		int scale = Utils.getScaleFactor();
		int mouseXS = mouseX * sr.getScaleFactor() / scale;
		int mouseYS = mouseY * sr.getScaleFactor() / scale;

		super.mouseClickMove(mouseXS, mouseYS, mouseButton, timeSinceLastClick);
		if(this.unicodeToggle.toggled) {
			this.unicodeTable.mouseClickMove(mouseX, mouseY, mouseButton, timeSinceLastClick);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button.id < FORMAT_COUNT) {
			this.editor.insertAtCursor(Utils.getFormatString(((GuiButtonFormat)button).format));
		} else if(button == this.rainbowToggle) {
			if(this.rainbowToggle.toggle()) {
				this.rainbow = RainbowLoop.INSTANCE.iterator();
			}
		} else if(button == this.unicodeToggle) {
			if(this.unicodeToggle.toggle()) {
				ScaledResolution sr = new ScaledResolution(Constants.MINECRAFT);
				int scale = Utils.getScaleFactor();

				final int x = (this.unicodeToggle.xPosition + this.unicodeToggle.width / 2) * scale / sr.getScaleFactor() - this.unicodeTable.getWidth() / 2;
				int y;
				if(this.parent instanceof GuiChat && !ColorCodeGUI.INSTANCE.config.top) {
					y = this.unicodeToggle.yPosition * scale / sr.getScaleFactor() - this.unicodeTable.getHeight() - Constants.SPACING;
				} else {
					y = (this.unicodeToggle.yPosition + this.unicodeToggle.height) * scale / sr.getScaleFactor() + Constants.SPACING;
				}
				this.unicodeTable.setPos(x, y);
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		ScaledResolution sr = new ScaledResolution(Constants.MINECRAFT);
		int scale = Utils.getScaleFactor();
		int mouseXS = mouseX * sr.getScaleFactor() / scale;
		int mouseYS = mouseY * sr.getScaleFactor() / scale;

		GL11.glPushMatrix();
		GL11.glScalef((float)scale / sr.getScaleFactor(), (float)scale / sr.getScaleFactor(), 1.0f);
		super.drawScreen(mouseXS, mouseYS, partialTicks);

		if(!this.unicodeToggle.toggled || !this.unicodeTable.isMouseOver(mouseX, mouseY)) {
			for(int i = 0; i < this.buttonList.size(); ++i) {
				if(this.buttonList.get(i) instanceof GuiButtonTooltip) {
					if(((GuiButtonTooltip)this.buttonList.get(i)).drawTooltip(mc, mouseXS, mouseYS)) {
						break;
					}
				}
			}
		}
		GL11.glPopMatrix();

		if(this.unicodeToggle.toggled) {
			this.unicodeTable.drawUnicodeTable(mc, mouseX, mouseY);
		}
	}
}
