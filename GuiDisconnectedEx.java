package net.rotten194.ccrelog;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.StringTranslate;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDisconnectedEx extends GuiScreen {
	static final Field SERVER_DATA = AutoRelog.getField(GuiMultiplayer.class, "field_74031_w", "w", "theServerData");
	private String errorMessage;
	private String errorDetail;
	private Object[] field_74247_c; // Arguments to String.format errorDetail
									// with
	private List field_74245_d;
	private final GuiScreen field_98095_n;
	private GuiGeneralSlider timeSlider;
	private long timeDisplayed;

	public GuiDisconnectedEx(GuiScreen parentScreen, String errorMessage, String errorDetail, Object... details) {
		StringTranslate stringtranslate = StringTranslate.getInstance();
		this.field_98095_n = parentScreen;
		this.errorMessage = stringtranslate.translateKey(errorMessage);
		this.errorDetail = errorDetail;
		this.field_74247_c = details;
	}

	ServerData getServerData() {
		return AutoRelog.serverData;
	}

	protected void keyTyped(char par1, int par2) {
	}

	public void initGui() {
		timeDisplayed = Minecraft.getSystemTime();
		StringTranslate stringtranslate = StringTranslate.getInstance();
		buttonList.clear();
		String toMenu = stringtranslate.translateKey("gui.toMenu");
		buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 120 + 12, toMenu));
		if (AutoRelog.getRelogTime(getServerData().serverName, getServerData().serverIP) < 0) {
			timeSlider = new GuiGeneralSlider(1, width / 2 - 100, height / 4 + 120 + 37, 0) {
				@Override
				public String updateDisplayText() {
					int min = 5;
					int max = 60;
					int current = (int) (sliderValue * (max - min)) + min;
					return "Reconnect after " + current + " seconds";
				}
			};
			buttonList.add(timeSlider);
			buttonList.add(new GuiButton(2, width / 2 + 55, height / 4 + 120 + 37, 45, 20, "Set"));
		} else {
			buttonList.add(new GuiButton(3, width / 2 - 100, height / 4 + 120 + 62, "Cancel"));
		}
		if (field_74247_c != null) {
			String error = stringtranslate.translateKeyFormat(errorDetail, field_74247_c);
			field_74245_d = fontRenderer.listFormattedStringToWidth(error, width - 50);
		} else {
			String errDetail = stringtranslate.translateKey(errorDetail);
			field_74245_d = fontRenderer.listFormattedStringToWidth(errDetail, width - 50);
		}
	}

	protected void actionPerformed(GuiButton button) {
		if (button.id == 0) {
			mc.displayGuiScreen(field_98095_n);
		} else if (button.id == 2) {
			ServerData data = getServerData();
			if (data != null)
				AutoRelog.setRelogTime(data.serverName, data.serverIP, (int) (timeSlider.sliderValue * (60 - 5)) + 5);
			reconnectToServer(data);
		} else if (button.id == 3) {
			ServerData data = getServerData();
			if (data != null)
				AutoRelog.clearRelogTime(data.serverName, data.serverIP);
			Minecraft.getMinecraft().displayGuiScreen(new GuiDisconnectedEx(field_98095_n, errorMessage, errorDetail, field_74247_c));
		}
	}

	void reconnectToServer(ServerData data) {
		mc.displayGuiScreen(new GuiConnecting(field_98095_n, this.mc, data));
	}

	public void drawScreen(int par1, int par2, float par3) {
		drawDefaultBackground();
		drawCenteredString(fontRenderer, errorMessage, width / 2, height / 2 - 50, 11184810);
		
		ServerData data = getServerData();
		int waitTime = AutoRelog.getRelogTime(data.serverName, data.serverIP);
		if (waitTime >= 0) {
			long timeUntil = timeDisplayed + waitTime * 1000 - Minecraft.getSystemTime();
			if (timeUntil > 0) {
				drawCenteredString(fontRenderer, String.format("Reconnecting in %d second%s...", timeUntil / 1000 + 1, timeUntil >= 1000 ? "s" : ""), width / 2, height / 4 + 120 + 37, 11184810);
			} else {
				reconnectToServer(data);
			}
		}

		int k = height / 2 - 30;

		if (field_74245_d != null) {
			for (Iterator iterator = field_74245_d.iterator(); iterator.hasNext(); k += fontRenderer.FONT_HEIGHT) {
				String s = (String) iterator.next();
				drawCenteredString(fontRenderer, s, width / 2, k, 16777215);
			}
		}

		super.drawScreen(par1, par2, par3);
	}
}
