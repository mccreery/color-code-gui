package tk.nukeduck.colorcodegui;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.input.Mouse;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import static org.lwjgl.opengl.GL11.*;

@Mod(modid = "colorcodegui", version = "1.1", name = "Color Code GUI")

public class ColorCodeGUI extends Gui {
	private static Minecraft mc = Minecraft.getMinecraft();
	private static final char[] codes    = "0123456789abcdefklmnor".toCharArray();
	private static final char[] displays = "----------------RBSUI-".toCharArray();
	private static final char[] rainbowLoop = "4c6ea2b35d".toCharArray();
	private int nextInRainbow = 0;
	
	private static final String codePrefix = "&";
	
	private static GuiButton[] buttons = new GuiButton[codes.length];
	private static GuiButton rainbowToggle, unicodeTable;
	
	private static final ResourceLocation colorTexture = new ResourceLocation("colorcodes", "textures/gui/color.png"), rainbowTexture = new ResourceLocation("colorcodes", "textures/gui/rainbow.png");
	
	private static Field inputField = null;
	private static Method signMethod = null;
	
	@SideOnly(Side.CLIENT)
	@EventHandler
	public void init(FMLInitializationEvent event) {
		for(int i = 0; i < codes.length; i++) {
			buttons[i] = new GuiButton(0, 6 + (22 * i), 5, 20, 20, ChatFormatting.getByChar(codes[i]) + "" + displays[i]);
		}
		rainbowToggle = new GuiButton(0, buttons[buttons.length - 1].xPosition + 25, 5, 20, 20, "");
		unicodeTable = new GuiButton(0, buttons[buttons.length - 1].xPosition + 50, 5, 40, 20, "UCode");
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	private static boolean isPressed = false, isRainbow = false, unicodeOpen = false;
	private static int currentChatLength = 0;
	
	private static int unicodeX = 100, unicodeY = 100, dragX, dragY;
	
	private static boolean isDragging = false, isScrolling = false;
	private static int scrollHeight = 0;
	
	private static char[] chars = ("\u00A2\u00A3\u00A4\u00A5"
		+ "\u00A1\u00A7\u00A9\u00AE\u00B0\u00B1\u2070\u00B9"
		+ "\u00B2\u00B3\u2074\u2075\u2076\u2077\u2078\u2079"
		+ "\u00BD\u2153\u2154\u00BC\u00BE\u215B\u215C\u215D"
		+ "\u215E\u00B5\u00B6\u00B7\u00BB\u00D7\u00F7\u00F8"
		+ "\u02C2\u02C3\u02C4\u02C5\u1D25\u2022\u2020\u2026"
		+ "\u2030\u2039\u203A\u203D\u205E\u20AA\u2116\u2122"
		+ "\u212E\u2126\u2190\u2191\u2192\u2193\u2194\u2195"
		+ "\u2211\u221A\u221E\u2248\u2260\u2261\u2264\u2265"
		+ "\u2500\u2502\u250C\u2510\u2514\u2518\u251C\u2524"
		+ "\u252C\u2534\u253C\u2550\u2551\u2552\u2553\u2554"
		+ "\u2555\u2556\u2557\u2558\u2559\u255A\u255B\u255C"
		+ "\u255D\u255E\u255F\u2560\u2561\u2562\u2563\u2564"
		+ "\u2565\u2566\u2567\u2568\u2569\u256A\u256B\u256C"
		+ "\u2580\u2584\u2588\u258C\u2590\u2591\u2592\u2593"
		+ "\u25A0\u25A1\u25AA\u25AB\u25AC\u25B2\u25BA\u25BC"
		+ "\u25C4\u25CA\u25CB\u25CF\u263A\u263B\u263C\u2640"
		+ "\u2642\u2660\u2663\u2665\u2666\u266A\u266B\u266F"
		+ "\u2600\u2601\u2602\u2603\u2604\u260E\u260F\u2610"
		+ "\u2611\u2612\u260D\u3010\u30C4\u3011\u261A\u261B"
		+ "\u261C\u261D\u261E\u261F\u2620\u2621\u2622\u2623"
		+ "\u2624\u2639\u262E\u262F\u263D\u263E\u2701\u2702"
		+ "\u2703\u2704\u2706\u2708\u2709\u270C\u270D\u270E"
		+ "\u270F\u2710\u2711\u2712\u2713\u2714\u2717\u2718"
		+ "\u2756\u2764\u2765\u2668\u2103\u2109\u278A\u278B"
		+ "\u278C\u278D\u278E\u278F\u2790\u2791\u2792\u2793"
		+ "\u24EA\u2460\u2461\u2462\u2463\u2464\u2465\u2466"
		+ "\u2467\u2468\u2469\u246A\u246B\u246C\u246D\u246E"
		+ "\u246F\u2470\u2471\u2472\u2473\u24B6\u24B7\u24B8"
		+ "\u24B9\u24BA\u24BB\u24BC\u24BD\u24BE\u24BF\u24C0"
		+ "\u24C1\u24C2\u24C3\u24C4\u24C5\u24C6\u24C7\u24C8"
		+ "\u24C9\u24CA\u24CB\u24CC\u24CD\u24CE\u24CF\u24D0"
		+ "\u24D1\u24D2\u24D3\u24D4\u24D5\u24D6\u24D7\u24D8"
		+ "\u24D9\u24DA\u24DB\u24DC\u24DD\u24DE\u24DF\u24E0"
		+ "\u24E1\u24E2\u24E3\u24E4\u24E5\u24E6\u24E7\u24E8"
		+ "\u24E9\u265A\u265B\u265C\u265D\u265E\u265F\u27B4"
		+ "\u27B5\u27B6\u27B7\u27B8\u27B9\u27B2\u21AD\u21E6"
		+ "\u21E7\u21E8\u21E9\u21EA\u2605\u272A\u25A3\u25C9"
		+ "\u25DC\u25DD\u25DE\u25DF\u25E0\u25E1\u25E2\u25E3"
		+ "\u25E4\u25E5\u2042").toCharArray();
	
	@SubscribeEvent
	public void onOpenGUI(GuiOpenEvent e) {
		if(e.gui != null) {
			if(e.gui instanceof GuiEditSign) {
				if(signMethod == null) {
					try {
						Method[] methods = e.gui.getClass().getDeclaredMethods();
						
						for(Method method : methods) {
							Class<?>[] params = method.getParameterTypes();
							if(!ArrayUtils.contains(params, char.class) || !ArrayUtils.contains(params, int.class)
								/*|| !ArrayUtils.contains(method.getExceptionTypes(), IOException.class)*/) continue;
							
							signMethod = method;
							signMethod.setAccessible(true);
							return;
						}
						if(signMethod == null) System.err.println("Sign Method is null. This will not go well.");
					} catch (Exception ex) {ex.printStackTrace();}
				}
			} else {
				try {
					Field[] fields = e.gui.getClass().getDeclaredFields();
					
					for(Field field : fields) {
						if(field.getType() == GuiTextField.class) {
							inputField = field;
							inputField.setAccessible(true);
							return;
						}
					}
				} catch (Exception ex) {ex.printStackTrace();}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderGui(RenderGameOverlayEvent.Chat e) {
		boolean isSign = false;
		if(mc.currentScreen != null && (mc.currentScreen instanceof GuiChat || (isSign = mc.currentScreen instanceof GuiEditSign) || mc.currentScreen instanceof GuiRepair || mc.currentScreen instanceof GuiCommandBlock)) {
			ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			
			//-------------------------------------------REFLECTION STUFF
			
			if(isRainbow && !isSign) {
				GuiTextField currentInput = null;
				try {
					currentInput = (GuiTextField) inputField.get(mc.currentScreen);
				} catch(Exception e1) {}
				
				String s = currentInput.getText();
				int l = s.length();
				
				if(l > currentChatLength) {
					if(!s.substring(l - 1).equals(" ")) {
						currentInput.setText(s.substring(0, l - 1) + codePrefix + rainbowLoop[nextInRainbow] + s.substring(l - 1));
						nextInRainbow++;
						if(nextInRainbow >= rainbowLoop.length) nextInRainbow = 0;
					}
					currentChatLength = l + 2;
				} else if(l < currentChatLength) {
					currentChatLength = l;
				}
			}
			
			int mouseX = Mouse.getX();
			int mouseY = mc.displayHeight - Mouse.getY();
			
			int scale = sr.getScaleFactor();
			glPushMatrix();
			
			//------------------------------------------------ UNICODE TABLE
			
			if(unicodeOpen) {
				int mouseXS = mouseX / scale;
				int mouseYS = mouseY / scale;
				
				int width = 160, height = 200;
				int tableStartY = unicodeY + mc.fontRendererObj.FONT_HEIGHT + 10;
				
				int right = unicodeX + width;
				int down = unicodeY + height;
				renderBox(unicodeX, unicodeY, width, height + 2, 0x55000000);
				
				this.drawGradientRect(unicodeX, unicodeY + 5, right, tableStartY - 1, 0x99666666, 0x99000000);
				
				this.drawRect(right, tableStartY, right + 10, down, 0x99000000);
				
				int scrollY = height - mc.fontRendererObj.FONT_HEIGHT - 48;
				
				if(isScrolling) {
					scrollHeight = Math.min(scrollY, Math.max(0, mouseYS - tableStartY - 19));
				} else if(Mouse.isButtonDown(0) && !isPressed && !isScrolling
					&& mouseXS >= right && mouseXS <= right + 10
					&& mouseYS >= tableStartY && mouseYS <= down) {
					isScrolling = true;
				}
				if(!Mouse.isButtonDown(0) && isScrolling) isScrolling = false;
				
				int barY = tableStartY + scrollHeight + 3;
				
				renderBox(right + 2, barY, 6, 32, 0x55ffffff);
				
				this.drawString(mc.fontRendererObj, "Unicode Table", unicodeX + 5, unicodeY + 8, 0xffffff);
				
				int perLine = (width - 10) / 10;
				
				int charOffset = Math.max(0, Math.round(((float) scrollHeight / scrollY) * ((int) (chars.length / perLine) - (int) ((height - mc.fontRendererObj.FONT_HEIGHT - 20) / 10) + 5)) * perLine);
				int max = Math.min(chars.length, charOffset + perLine * ((height - mc.fontRendererObj.FONT_HEIGHT - 20) / 10));
				
				String name = "";
				for(int i = charOffset; i < max; i++) {
					char currentChar = chars[i];
					
					int a = unicodeX + 5 + ((i - charOffset) % perLine * 10);
					int b = tableStartY + 5 + ((i - charOffset) / perLine * 10);
					
					boolean hover = mouseXS >= a && mouseXS < a + 10
						&& mouseYS >= b && mouseYS < b + 10;
					
					if(hover) {
						name = Character.getName(currentChar);
						
						if(!isPressed && Mouse.isButtonDown(0)) {
							writeText(mc, "" + currentChar, isSign);
						}
					}
					
					glEnable(GL_BLEND);
					
					glColor4f(1, 1, 1, hover ? 1 : 0.5F);
					mc.getTextureManager().bindTexture(new ResourceLocation("colorcodes", "textures/gui/button.png"));
					drawImage(a, b, 10, 10);
					this.drawString(mc.fontRendererObj, "" + currentChar, a + (11 - mc.fontRendererObj.getStringWidth("" + chars[i])) / 2, b + 1, 0xffffffff);
				}
				
				if(name != "") {
					renderBox(mouseXS + 10, mouseYS - 5, mc.fontRendererObj.getStringWidth(name) + 10, mc.fontRendererObj.FONT_HEIGHT + 10, 0x99000000);
					this.drawString(mc.fontRendererObj, name, mouseXS + 15, mouseYS, 0xffffff);
				}
				
				if(!isPressed && !isDragging
					&& mouseXS >= unicodeX && mouseXS <= unicodeX + width
					&& mouseYS >= unicodeY && mouseYS <= tableStartY
					&& Mouse.isButtonDown(0)) {
					isDragging = true;
					dragX = mouseXS - unicodeX;
					dragY = mouseYS - unicodeY;
				}
				
				if(isDragging) {
					unicodeX = mouseXS - dragX;
					unicodeY = mouseYS - dragY;
					if(!Mouse.isButtonDown(0)) {
						isDragging = false;
					}
				}
				
				if(unicodeX < 0) unicodeX = 0;
				else if(unicodeX > sr.getScaledWidth() - width - 10) unicodeX = sr.getScaledWidth() - width - 10;
				
				if(unicodeY < 0) unicodeY = 0;
				else if(unicodeY > sr.getScaledHeight() - height - 2) unicodeY = sr.getScaledHeight() - height - 2;
			}
			
			glScalef(1.0F / scale, 1.0F / scale, 1F);
			
			int buttonOffsetY = mc.currentScreen instanceof GuiChat ? mc.displayHeight - 25 * scale : 6;
			
			//---------------------------------------- RAINBOW BUTTON
			
			rainbowToggle.yPosition = buttonOffsetY;
			rainbowToggle.drawButton(mc, mouseX, mouseY);
			
			mc.getTextureManager().bindTexture(rainbowTexture);
			
			float bright = isRainbow ? 1 : 0.5F;
			glColor3f(bright, bright, bright);
			drawImage(rainbowToggle);
			
			if(rainbowToggle.mousePressed(mc, mouseX, mouseY)) {
				glScalef(2, 2, 1);
				mc.currentScreen.drawString(mc.fontRendererObj, "Rainbow Mode", rainbowToggle.xPosition / 2, (rainbowToggle.yPosition + rainbowToggle.height) / 2 + 5, 0xffffff);
				glScalef(0.5F, 0.5F, 1);
				
				if(!isPressed && Mouse.isButtonDown(0)) {
					isRainbow = !isRainbow;
					nextInRainbow = 0;
				}
			}
			
			//---------------------------------------- UNICODE BUTTON
			
			unicodeTable.yPosition = buttonOffsetY;
			unicodeTable.drawButton(mc, mouseX, mouseY);
			
			if(unicodeTable.mousePressed(mc, mouseX, mouseY)) {
				glScalef(2, 2, 1);
				mc.currentScreen.drawString(mc.fontRendererObj, "Unicode Table", unicodeTable.xPosition / 2, (unicodeTable.yPosition + unicodeTable.height) / 2 + 5, 0xffffff);
				glScalef(0.5F, 0.5F, 1);
				
				if(!isPressed && Mouse.isButtonDown(0)) {
					unicodeOpen = !unicodeOpen;
				}
			}
			
			//---------------------------------------- OTHER BUTTONS
			
			for(int i = 0; i < buttons.length; i++) {
				GuiButton button = buttons[i];
				button.yPosition = buttonOffsetY;
				
				button.drawButton(mc, mouseX, mouseY);
				if(i < 16) {
					mc.getTextureManager().bindTexture(colorTexture);
					drawImage(button);
				}
				
				if(button.mousePressed(mc, mouseX, mouseY)) {
					char currentCode = codes[i];
					
					glScalef(2, 2, 1);
					mc.currentScreen.drawString(mc.fontRendererObj, WordUtils.capitalizeFully(ChatFormatting.getByChar(currentCode).name().replace("_", " ").toLowerCase()), button.xPosition / 2, (button.yPosition + button.height) / 2 + 5, 0xffffff);
					glScalef(0.5F, 0.5F, 1);
					
					if(!isPressed && Mouse.isButtonDown(0)) {
						writeText(mc, codePrefix + currentCode, isSign);
					}
				}
			}
			glPopMatrix();
			
			isPressed = Mouse.isButtonDown(0);
		}
	}
	
	private void writeText(Minecraft mc, String text, boolean isSign) {
		if(isSign) {
			if(signMethod != null) {
				try {
					for(char c : text.toCharArray()) {
							signMethod.invoke(mc.currentScreen, c, (int) c);
					}
				} catch (Exception e) {e.printStackTrace();}
			} else {System.err.println("Couldn't write to sign.");}
		} else if(inputField != null) {
			GuiTextField currentInput = null;
			try {
				((GuiTextField) inputField.get(mc.currentScreen)).writeText(text);
			} catch(Exception e) {e.printStackTrace();}
		}
	}
	
	private static Tessellator t = Tessellator.getInstance();
	private static WorldRenderer w = t.getWorldRenderer();
	
	@SideOnly(Side.CLIENT)
	private static void drawImage(GuiButton button) {
		drawImage(button.xPosition + 2, button.yPosition + 2, 16, 16);
	}
	
	@SideOnly(Side.CLIENT)
	private static void drawImage(int x, int y, int width, int height) {
		w.startDrawingQuads();
		w.addVertexWithUV(x, y, 0, 0, 0);
		w.addVertexWithUV(x, y + height, 0, 0, 1);
		w.addVertexWithUV(x + width, y + height, 0, 1, 1);
		w.addVertexWithUV(x + width, y, 0, 1, 0);
		t.draw();
	}
	
	public void renderBox(int x, int y, int width, int height, int color) {
		this.drawRect(x + 1, y, x + width - 1, y + height, color);
		this.drawRect(x, y + 1, x + 1, y + height - 1, color);
		this.drawRect(x + width - 1, y + 1, x + width, y + height - 1, color);
		
		this.drawRect(x + 1, y + 1, x + (width - 1), y + 2, color);
		this.drawRect(x + 1, y + height - 2, x + (width - 1), y + height - 1, color);
		
		this.drawRect(x + 1, y + 2, x + 2, y + height - 2, color);
		this.drawRect(x + (width - 2), y + 2, x + (width - 1), y + height - 2, color);
	}
}