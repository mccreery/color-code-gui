package nukeduck.ccgui.util;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public abstract class ITextEditor implements CharSequence {
	public final void append(String text) {
		this.insert(this.length(), text);
	}

	public final void insert(int pos, String text) {
		this.insert(pos, text, 0, text.length());
	}
	public final void insertAtCursor(String text) {
		this.insertAtCursor(text, 0, text.length());
	}

	public final void deleteSelection() {
		this.insertAtCursor("");
	}
	public final void insertAtCursor(String text, int start, int end) {
		int selectStart = this.getSelectStart(), selectEnd = this.getSelectEnd();

		if(selectStart > selectEnd) {
			int temp = selectStart;
			selectStart = selectEnd;
			selectEnd = temp;
		}

		if(selectStart != selectEnd) {
			this.delete(selectStart, selectEnd);
		}
		this.insert(selectStart, text, start, end);
		this.setCursor(selectStart + (end - start));
	}

	public final void replace(int start, int end, String text) {
		this.replace(start, end, text, 0);
	}

	public final void clear() {
		this.delete(0, this.length());
	}
	public final void setCursor(int pos) {
		this.setSelection(pos, pos);
	}

	@Override
	public final String toString() {
		return this.subSequence(0, this.length()).toString();
	}

	public abstract void insert(int pos, String text, int start, int end);
	public abstract void replace(int start, int end, String text, int offset);
	public abstract void delete(int start, int end);
	public abstract int getSelectStart();
	public abstract int getSelectEnd();
	public abstract void setSelection(int start, int end);

	public static final ITextEditor create(GuiScreen screen) {
		try {
			if(screen instanceof GuiEditSign) {
				// Geez, Java. Calm it with the line length.
				TileEntitySign sign = ObfuscationReflectionHelper.<TileEntitySign, GuiEditSign>getPrivateValue(GuiEditSign.class, (GuiEditSign)screen, "field_146848_f", "tileSign");
				Field line = ReflectionHelper.findField(GuiEditSign.class, ObfuscationReflectionHelper.remapFieldNames(GuiEditSign.class.getName(), "field_146851_h", "editLine"));

				if(sign != null && line != null) {
					return new TextEditorSign((GuiEditSign)screen, sign, line);
				}
			} else if(screen != null) {
				List<GuiTextField> textFields = Utils.getFields(screen, GuiTextField.class, true);

				if(textFields.size() > 0) {
					return new TextEditorGui(textFields.toArray(new GuiTextField[textFields.size()]));
				}
			}
		} catch(Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public static class TextEditorGui extends ITextEditor {
		private final GuiTextField[] fields;
		public TextEditorGui(GuiTextField... fields) {
			this.fields = fields;
		}

		private GuiTextField getFocused() {
			for(GuiTextField field : this.fields) {
				if(field.isFocused()) return field;
			}
			return null;
		}

		@Override
		public char charAt(int index) {
			GuiTextField focused = this.getFocused();
			if(focused == null) return '\0';
			return focused.getText().charAt(index);
		}
		@Override
		public int length() {
			GuiTextField focused = this.getFocused();
			if(focused == null) return 0;
			return focused.getText().length();
		}
		@Override
		public CharSequence subSequence(int start, int end) {
			GuiTextField focused = this.getFocused();
			if(focused == null) return "";
			return focused.getText().subSequence(start, end);
		}

		@Override
		public void insert(int pos, String text, int start, int end) {
			GuiTextField focused = this.getFocused();
			StringBuilder builder = new StringBuilder(
				focused.getText().length() + (end - start));

			builder.append(focused.getText(), 0, pos);
			builder.append(text, start, end);
			builder.append(focused.getText(), pos, focused.getText().length());

			focused.setText(builder.toString());
		}

		@Override
		public void replace(int start, int end, String text, int offset) {
			GuiTextField focused = this.getFocused();
			StringBuilder builder = new StringBuilder(
				Math.max(focused.getText().length(), end));

			builder.append(focused.getText(), 0, start);
			builder.append(text, offset, offset + (end - start));
			builder.append(focused.getText(), end, focused.getText().length());

			focused.setText(builder.toString());
		}

		@Override
		public void delete(int start, int end) {
			GuiTextField focused = this.getFocused();
			StringBuilder builder = new StringBuilder(
				focused.getText().length() - (end - start));

			builder.append(focused.getText(), 0, start);
			builder.append(focused.getText(), end, focused.getText().length());

			focused.setText(builder.toString());
		}

		@Override
		public int getSelectStart() {
			GuiTextField focused = this.getFocused();
			if(focused == null) return 0;
			return focused.getCursorPosition();
		}
		@Override
		public int getSelectEnd() {
			GuiTextField focused = this.getFocused();
			if(focused == null) return 0;
			return focused.getSelectionEnd();
		}
		@Override
		public void setSelection(int start, int end) {
			GuiTextField focused = this.getFocused();
			if(focused == null) return;
			focused.setSelectionPos(start);
			focused.setCursorPosition(end);
		}
	}

	public static class TextEditorSign extends ITextEditor {
		private final GuiEditSign gui;
		private final TileEntitySign sign;
		private final Field line;

		public TextEditorSign(GuiEditSign gui, TileEntitySign sign, Field line) {
			this.gui = gui;
			this.sign = sign;
			this.line = line;
		}

		private int getLine() {
			int line = 0;
			try {
				line = this.line.getInt(gui);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return line & 3;
		}

		private String getText() {
			return this.sign.signText[this.getLine()].getUnformattedText();
		}
		private void setText(String text) {
			this.sign.signText[this.getLine()] = new TextComponentString(text);
		}

		@Override
		public char charAt(int index) {
			return this.getText().charAt(index);
		}
		@Override
		public int length() {
			return this.getText().length();
		}
		@Override
		public CharSequence subSequence(int start, int end) {
			return this.getText().subSequence(start, end);
		}

		@Override
		public void insert(int pos, String text, int start, int end) {
			String line = this.getText();

			StringBuilder builder = new StringBuilder(
				line.length() + (end - start));

			builder.append(line, 0, pos);
			builder.append(text, start, end);
			builder.append(line, pos, line.length());

			this.setText(builder.toString());
		}

		@Override
		public void replace(int start, int end, String text, int offset) {
			String line = this.getText();
			StringBuilder builder = new StringBuilder(
				Math.max(line.length(), end));

			builder.append(line, 0, start);
			builder.append(text, offset, offset + (end - start));
			builder.append(line, end, line.length());

			this.setText(builder.toString());
		}

		@Override
		public void delete(int start, int end) {
			String line = this.getText();
			StringBuilder builder = new StringBuilder(
				line.length() - (end - start));

			builder.append(line, 0, start);
			builder.append(line, end, line.length());

			this.setText(builder.toString());
		}

		@Override
		public int getSelectStart() {
			return this.getText().length();
		}
		@Override
		public int getSelectEnd() {
			return this.getText().length();
		}
		@Override
		public void setSelection(int start, int end) {}
	}
}
