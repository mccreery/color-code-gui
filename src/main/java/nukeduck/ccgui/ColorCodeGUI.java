package nukeduck.ccgui;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import nukeduck.ccgui.gui.GuiColorCodes;
import nukeduck.ccgui.util.ITextEditor;

@Mod(modid = ColorCodeGUI.MODID, version = "1.2", name = "Color Code GUI", clientSideOnly = true)
public class ColorCodeGUI {
	public static final String MODID = "ccgui";
	public static final Minecraft MC = Minecraft.getMinecraft();
	public static final ResourceLocation ICONS = new ResourceLocation(MODID, "textures/gui/icons.png");

	public static Config config;

	private GuiColorCodes gui;

	@EventHandler
	public void init(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		config = new Config(event.getSuggestedConfigurationFile());
	}

	@SubscribeEvent
	public void onOpenGui(GuiOpenEvent event) {
		if(event.getGui() == null) gui = null;
	}

	@SubscribeEvent
	public void onInitGui(InitGuiEvent.Post event) {
		if(event.getGui() != gui) {
			ITextEditor editor = ITextEditor.create(event.getGui());

			if(editor != null) {
				gui = new GuiColorCodes(event.getGui(), editor);

				ScaledResolution sr = new ScaledResolution(MC);
				gui.setWorldAndResolution(MC, sr.getScaledWidth(), sr.getScaledHeight());
			}
		}
	}

	@SubscribeEvent
	public void onRender(RenderTickEvent event) {
		if(event.phase != Phase.END) return;

		if(gui != null && !MC.skipRenderWorld) {
			final ScaledResolution sr = new ScaledResolution(MC);
			final int mouseX = Mouse.getX() / sr.getScaleFactor();
			final int mouseY = sr.getScaledHeight() - Mouse.getY() / sr.getScaleFactor() - 1;

			this.gui.drawScreen(mouseX, mouseY, event.renderTickTime);
		}
	}

	@SubscribeEvent
	public void onMouseInput(MouseInputEvent.Pre event) throws IOException {
		if(gui != null) gui.handleMouseInput();
	}

	@SubscribeEvent
	public void onKeyInput(KeyboardInputEvent.Post event) throws IOException {
		if(gui != null) gui.handleKeyboardInput();
	}
}
