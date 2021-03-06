package tae.cosmetics;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import tae.cosmetics.config.ConfigManager;
import tae.cosmetics.exceptions.TAEModException;
import tae.cosmetics.gui.GuiScreenStalkerMod;
import tae.cosmetics.util.API2b2tdev;
import tae.cosmetics.util.RebaneGetter;
import tae.cosmetics.util.TrustManagerSetup;


@Mod(modid = TAECosmetics.MODID, useMetadata = true)
public class TAECosmetics implements Globals
{
    public static final String MODID = "taecosmetics";

    public static TAECosmetics INSTANCE;
    
    public TAECosmetics() {
    	INSTANCE = this;
    }
    
    @EventHandler
    public void preInit(FMLInitializationEvent event) {
      
    	TrustManagerSetup.initTrustManager();
    	    	
    	RebaneGetter.init();
		API2b2tdev.update();
    	
    	new Thread(() -> {
        	ConfigManager.load();
    		GuiScreenStalkerMod.updateResourceMap();
    		changeMainMenu();
    	}).run();
    	
    	ModLoader ini = new ModLoader();
    	ini.searchPackage(modPath);
    	ini.init();
    	    	    	    	    	
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	if(event.getSide() == Side.CLIENT) {
    		MinecraftForge.EVENT_BUS.register(new tae.packetevent.ChannelHandlerInput());
    		MinecraftForge.EVENT_BUS.register(new OnLogin());
    	}
    }
    
    @EventHandler
    public void postInit(FMLInitializationEvent event) {
    	Runtime.getRuntime().addShutdownHook(new Thread(() -> ConfigManager.save()));
    }
    
    private static void changeMainMenu() {

    	final ResourceLocation TEMPLARFACE = new ResourceLocation("taecosmetics","textures/gui/main/templarface.png");
    	final ResourceLocation TEMPLARSIDE = new ResourceLocation("taecosmetics","textures/gui/main/templarside.png");
    	final ResourceLocation TEMPLARPANO1 = new ResourceLocation("taecosmetics","textures/gui/main/pano1.png");
    	final ResourceLocation TEMPLARPANO2 = new ResourceLocation("taecosmetics","textures/gui/main/pano2.png");
    	final ResourceLocation TEMPLARPANO3 = new ResourceLocation("taecosmetics","textures/gui/main/pano3.png");
    	final ResourceLocation TEMPLARBOTTOM = new ResourceLocation("taecosmetics","textures/gui/main/templarbottom.png");
    	final ResourceLocation TEMPLARTOP = new ResourceLocation("taecosmetics","textures/gui/main/templartop.png");
    	final ResourceLocation TEMPLAREDITION = new ResourceLocation("taecosmetics","textures/gui/main/edition.png");

    	Class<?> clazz = GuiMainMenu.class;

		try {
			
			Field minecraft = clazz.getDeclaredField("field_194400_H");
			
			minecraft.setAccessible(true);

			Field backgroundLocations = clazz.getDeclaredField("field_73978_o");
			
			backgroundLocations.setAccessible(true);
			
			Field modifiers = Field.class.getDeclaredField("modifiers");
			
			modifiers.setAccessible(true);
			
			modifiers.setInt(minecraft, minecraft.getModifiers() & ~Modifier.FINAL);
			
			minecraft.set(null, TEMPLAREDITION);
			
			modifiers.setInt(minecraft, minecraft.getModifiers() | Modifier.FINAL);
						
			minecraft.setAccessible(false);
			
			modifiers.setInt(backgroundLocations, backgroundLocations.getModifiers() & ~Modifier.FINAL);
			
			backgroundLocations.set(null, new ResourceLocation[] {
					TEMPLARSIDE, TEMPLARFACE, TEMPLARSIDE, TEMPLARFACE, TEMPLARTOP, TEMPLARBOTTOM
			});
			
			modifiers.setInt(backgroundLocations, backgroundLocations.getModifiers() | Modifier.FINAL);
			
			backgroundLocations.setAccessible(false);
			
			modifiers.setAccessible(false);
					
		} catch (Exception e) {
			OnLogin.addError(new TAEModException(e.getClass(), e.getMessage()));
		}
	
    }
}
