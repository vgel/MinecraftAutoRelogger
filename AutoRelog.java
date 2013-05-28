package net.rotten194.ccrelog;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(version = "0.1 (1.5.2)", modid = "AutoRelog", name = "Auto Relog")
public class AutoRelog {
	static Map<String, Integer> relogTimes;
	public static ServerData serverData;

	public static Field getField(Class<?> clz, String... names) {
		try {
			for (String name : names) {
				try {
					Field f = clz.getDeclaredField(name);
					f.setAccessible(true);
					return f;
				} catch (NoSuchFieldException e) {
				}
			}
			throw new RuntimeException(new NoSuchFieldException("None of " + Arrays.toString(names)));
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@PreInit
	public void preinit(FMLPreInitializationEvent event) {
		relogTimes = new HashMap<String, Integer>();
	}

	@Init
	public void init(FMLInitializationEvent event) {
		System.out.println("-------------INIT");
		MinecraftForge.EVENT_BUS.register(this);
		TickRegistry.registerTickHandler(new TickHandler(), Side.CLIENT);
	}

	public static void setRelogTime(String serverName, String serverIp, int to) {
		relogTimes.put(serverName + "\0" + serverIp, to);
	}

	public static int getRelogTime(String serverName, String serverIp) {
		String key = serverName + "\0" + serverIp;
		if (relogTimes.containsKey(key)) {
			return relogTimes.get(key);
		}
		return -1;
	}

	public static void clearRelogTime(String serverName, String serverIp) {
		String key = serverName + "\0" + serverIp;
		relogTimes.remove(key);
	}
}
