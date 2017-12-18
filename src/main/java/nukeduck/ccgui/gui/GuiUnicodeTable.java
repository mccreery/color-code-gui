package nukeduck.ccgui.gui;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.client.config.GuiUtils;
import nukeduck.ccgui.util.Constants;
import nukeduck.ccgui.util.ITextEditor;
import nukeduck.ccgui.util.Utils;

public class GuiUnicodeTable extends Gui {
	private static final int TITLE_HEIGHT = 2*Constants.SPACING + Constants.FONT_HEIGHT;

	private final String table = I18n.format(Constants.LANG_TABLE);
	private int x, y, width, height;

	private final ITextEditor editor;
	private final GuiScrollbar scrollbar;

	private boolean dragging;
	private int dragX, dragY;

	public GuiUnicodeTable(int x, int y, ITextEditor editor) {
		this.x = x;
		this.y = y;
		this.width = 170;
		this.height = 184;
		this.editor = editor;
		this.scrollbar = new GuiScrollbar(0, 0, 8, this.height - TITLE_HEIGHT - Constants.SPACING, this.getVisibleRows(), this.getRows());
		this.updateScrollBar();
	}

	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
		this.updateScrollBar();
	}
	public int getWidth() {
		return this.width;
	}
	public int getHeight() {
		return this.height;
	}

	protected boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX >= this.x && mouseX < this.x + this.width
			&& mouseY >= this.y && mouseY < this.y + this.height;
	}

	protected int getColumns() {
		return (this.width - 2*Constants.SPACING) / 10;
	}
	protected int getRows() {
		return (int)((float)this.table.length() / this.getColumns() + 0.5f);
	}
	protected int getVisibleRows() {
		return (this.height - TITLE_HEIGHT - Constants.SPACING) / 10;
	}

	protected int getHovered(int mouseX, int mouseY) {
		mouseX -= this.x + Constants.SPACING;
		mouseY -= this.y + TITLE_HEIGHT;

		if(mouseX < 0 || mouseX >= this.width - 2*Constants.SPACING
				|| mouseY < 0 || mouseY >= this.getVisibleRows() * 10) {
			return -1;
		}
		return (mouseY / 10 + this.scrollbar.getScroll()) * this.getColumns() + (mouseX / 10);
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(mouseButton != 0) return false;
		final int hovered = this.getHovered(mouseX, mouseY);

		if(hovered != -1) {
			this.editor.insertAtCursor(String.valueOf(table.charAt(hovered)));
			Constants.MINECRAFT.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			return true;
		} else if(this.isMouseOver(mouseX, mouseY)) {
			this.dragging = true;
			this.dragX = mouseX - this.x;
			this.dragY = mouseY - this.y;
			return true;
		} else {
			return this.scrollbar.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if(state == 0) {
			this.dragging = false;
		}
		this.scrollbar.mouseReleased(mouseX, mouseY, state);
	}

	protected void updateScrollBar() {
		this.scrollbar.setPos(this.x + this.width, this.y + TITLE_HEIGHT);
	}

	protected void mouseClickMove(int mouseX, int mouseY, int mouseButton,
			long timeSinceLastClick) {
		if(mouseButton != 0) return;

		if(this.dragging) {
			this.x = mouseX - this.dragX;
			this.y = mouseY - this.dragY;
			this.updateScrollBar();
		} else {
			this.scrollbar.mouseClickMove(mouseX, mouseY, mouseButton,
				timeSinceLastClick);
		}
	}

	public void drawUnicodeTable(Minecraft mc, int mouseX, int mouseY) {
		//int right = this.x + this.width, bottom = this.y + this.height;

		drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x55000000);
		drawRect(this.x, this.y, this.x + this.width, this.y + Constants.FONT_HEIGHT + Constants.SPACING, 0x55000000);
		this.scrollbar.drawScrollbar();

		/*GraphicsUtils.renderBox(this.x, this.y,
			this.width, this.height, 0x55000000);*/
		/*this.drawGradientRect(this.x, this.y + SPACING,
			right, this.y + SPACING + ColorCodeGUI.FONT_HEIGHT,
			0x99666666, 0x99000000);*/

		this.drawString(mc.fontRendererObj, I18n.format("ccgui.unicodeTitle"),
			this.x + Constants.SPACING, this.y + Constants.SPACING, 0xffffff);

		this.drawContents(mc);

		int hovered = this.getHovered(mouseX, mouseY);
		if(hovered != -1 && hovered < this.table.length()) {
			final List<String> name = Arrays.asList(new String[] {
				Utils.getDisplayName(
					Character.getName(this.table.charAt(hovered)), ' ')
			});
			GuiUtils.drawHoveringText(name, mouseX, mouseY,
				mc.displayWidth, mc.displayHeight, -1, mc.fontRendererObj);
		}
	}

	protected void drawContents(Minecraft mc) {
		//final int maxX = this.getColumns(), maxY = this.getVisibleRows();

		int i = this.scrollbar.getScroll() * this.getColumns();
		int xPos, yPos = this.y + TITLE_HEIGHT;

		for(int y = 0; y < this.getVisibleRows(); y++) {
			xPos = this.x + Constants.SPACING;

			for(int x = 0; x < this.getColumns(); x++) {
				if(i >= table.length()) return;
				String current = String.valueOf(table.charAt(i++));

				mc.getTextureManager().bindTexture(Constants.ICONS);
				this.drawTexturedModalRect(xPos, yPos, 48, 0, 10, 10);
				this.drawCenteredString(mc.fontRendererObj, current, xPos + 5, yPos, 0xFFFFFFFF);

				xPos += 10;
			}
			yPos += 10;
		}
	}
}
