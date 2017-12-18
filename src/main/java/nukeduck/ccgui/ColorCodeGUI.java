package nukeduck.ccgui;

import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import nukeduck.ccgui.gui.GuiColorCodes;
import nukeduck.ccgui.util.Constants;
import nukeduck.ccgui.util.ITextEditor;

@Mod(modid=Constants.MODID, version="1.2", name="Color Code GUI", clientSideOnly=true)
public class ColorCodeGUI {
	@Instance(Constants.MODID)
	public static ColorCodeGUI INSTANCE;

	private GuiColorCodes gui;
	public Config config;

	@EventHandler
	public void init(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		this.config = new Config(event.getSuggestedConfigurationFile());
	}

	@SubscribeEvent
	public void onOpenGui(GuiOpenEvent event) {
		if(event.getGui() == null) this.gui = null;
	}

	@SubscribeEvent
	public void onInitGui(InitGuiEvent.Post event) {
		if(event.getGui() != this.gui) {
			ITextEditor editor = ITextEditor.create(event.getGui());

			if(editor != null) {
				this.gui = new GuiColorCodes(event.getGui(), editor);

				final ScaledResolution sr = new ScaledResolution(event.getGui().mc);
				this.gui.setWorldAndResolution(event.getGui().mc, sr.getScaledWidth(), sr.getScaledHeight());
			}
		}
	}

	@SubscribeEvent
	public void onRender(RenderTickEvent event) {
		if(event.phase != Phase.END) return;

		if(this.gui != null && !this.gui.mc.skipRenderWorld) {
			final ScaledResolution sr = new ScaledResolution(this.gui.mc);
			final int mouseX = Mouse.getX() / sr.getScaleFactor();
			final int mouseY = sr.getScaledHeight() - Mouse.getY() / sr.getScaleFactor() - 1;

			this.gui.drawScreen(mouseX, mouseY, event.renderTickTime);
		}
	}

	@SubscribeEvent
	public void onMouseInput(MouseInputEvent.Pre event) {
		if(this.gui != null) {
			try {
				this.gui.handleMouseInput();
			} catch(Throwable thrown) {
				thrown.printStackTrace();
			}
		}
	}

	@SubscribeEvent
	public void onKeyInput(KeyboardInputEvent.Post event) {
		if(this.gui != null) {
			try {
				this.gui.handleKeyboardInput();
			} catch(Throwable thrown) {
				thrown.printStackTrace();
			}
		}
	}
}
