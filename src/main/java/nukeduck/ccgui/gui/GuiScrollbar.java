package nukeduck.ccgui.gui;

import net.minecraft.client.gui.Gui;

public class GuiScrollbar extends Gui {
	protected static final int COLOR = 0x55000000;

	protected int x, y, width, height;

	protected int visible, total;
	protected float fac;

	public GuiScrollbar(int x, int y, int width, int height, int visible, int total) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.visible = visible;
		this.total = total;
		this.updateBarHeight();
	}

	public int getScroll() {
		return (int) (this.fac * (this.total - this.visible));
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX >= this.x && mouseX < this.x + this.width
			&& mouseY >= this.y && mouseY < this.y + this.height;
	}

	protected int getBarY() {
		return this.y + (int)((this.height - this.getBarHeight()) * this.fac);
	}

	protected int barHeight;
	protected int getBarHeight() {return this.barHeight;}
	protected void updateBarHeight() {
		this.barHeight = this.visible * this.height / this.total;
	}

	protected int dragStart = -1;

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(this.isMouseOver(mouseX, mouseY)) {
			this.dragStart = mouseY - (this.getBarY() - this.y);
			return true;
		}
		return false;
	}
	protected void mouseClickMove(int mouseX, int mouseY, int mouseButton,
			long timeSinceLastClick) {
		if(this.dragStart != -1) {
			this.fac = (float)(mouseY - this.dragStart) / (this.height - this.getBarHeight());
			this.fac = this.fac > 1.0f ? 1.0f : this.fac < 0.0f ? 0.0f : this.fac;
		}
	}
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		this.dragStart = -1;
	}

	public void drawScrollbar() {
		final int y = this.getBarY();
		drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x33000000);
		drawRect(this.x + 1, y + 1, this.x + this.width - 1, y + this.getBarHeight() - 1, 0x55000000);
	}

	public void setPane(int visible, int total) {
		this.visible = visible;
		this.total = total;
		this.updateBarHeight();
	}
	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		this.updateBarHeight();
	}

	public int getX() {return this.x;}
	public int getY() {return this.x;}
	public int getWidth() {return this.width;}
	public int getHeight() {return this.height;}
}
