package net.rotten194.ccrelog;
import java.lang.reflect.Field;
import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandler implements ITickHandler {
	static final EnumSet CLIENT = EnumSet.of(TickType.CLIENT);
	//Forge, at runtime, deobfuscates names to the stable MCP names. Nice, but unexpected. Leaving
	//original names in case I do port to modloader
	static final Field PARENT_SCREEN = AutoRelog.getField(GuiMultiplayer.class, "field_74025_c", "c", "parentScreen");
	static final Field PARENT_SCREEN1 = AutoRelog.getField(GuiDisconnected.class, "field_98095_n", "n");
	static final Field ERROR_MESSAGE = AutoRelog.getField(GuiDisconnected.class, "field_74248_a", "a", "errorMessage");
	static final Field ERROR_DETAILS = AutoRelog.getField(GuiDisconnected.class, "field_74246_b", "b", "errorDetail");
	static final Field ERROR_FORMAT = AutoRelog.getField(GuiDisconnected.class, "field_74247_c", "c");

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(CLIENT)) {
			GuiScreen guiscreen = Minecraft.getMinecraft().currentScreen;

			if (guiscreen instanceof GuiDisconnected) {
				try {
					GuiScreen parentScreen = (GuiScreen) PARENT_SCREEN1.get(guiscreen);
					String em = (String) ERROR_MESSAGE.get(guiscreen);
					String ed = (String) ERROR_DETAILS.get(guiscreen);
					Object[] ef = (Object[]) ERROR_FORMAT.get(guiscreen);
					Minecraft.getMinecraft().displayGuiScreen(new GuiDisconnectedEx(parentScreen, em, ed, ef));
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			} else if (guiscreen instanceof GuiMultiplayer) {
				try {
					GuiScreen parentScreen = (GuiScreen) PARENT_SCREEN.get(guiscreen);
					Minecraft.getMinecraft().displayGuiScreen(new GuiMultiplayerEx(parentScreen));
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return CLIENT;
	}

	@Override
	public String getLabel() {
		return null;
	}

}
