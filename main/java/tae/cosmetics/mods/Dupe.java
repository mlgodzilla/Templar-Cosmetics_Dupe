package tae.cosmetics.mods;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tae.cosmetics.gui.util.mainscreen.CancelPacketsGuiList;
import tae.cosmetics.gui.util.mainscreen.DupeStatus;
import tae.packetevent.PacketEvent;

import java.util.function.DoublePredicate;

public class Dupe extends BaseMod {

    private static DupeStatus gui = new DupeStatus("Dupe State:", 50, 50);

    public static boolean enabled = false;
    public static int dupestate = 0;
    private static int pchunkx;
    private static int pchunkz;
    private static Entity ridingEntity;

    public static void launch(){
        if (mc.player == null || mc.world == null) {
            return;
        }

        if(!enabled){
            dupestate = 0;
            dupeStart();
        }
    }

    public static void off()
    {
        if(ModifiedFreecam.enabled) {
            ModifiedFreecam.onDisabled();
        }
        if(CancelPacketMod.enabled) {
            CancelPacketMod.toggle();
        }
        enabled = false;
    }

    public static int getDupestate(){
        return dupestate;
    }
    public static void dupeStart()
    {
        if(!mc.player.isRiding())
        {
            enabled = false;
            return;
        }
        enabled = true;
        if(!ModifiedFreecam.enabled) {
            ModifiedFreecam.onEnabled();
        }
        ridingEntity = ModifiedFreecam.getRidingEntity();
        if(!CancelPacketMod.enabled) {
            CancelPacketMod.toggle();
        }
        EntityOtherPlayerMP originalPlayer = ModifiedFreecam.getOriginalPlayer();
        dupestate = 1;
        pchunkx = originalPlayer.getPosition().getX() >> 4;
        pchunkz = originalPlayer.getPosition().getZ() >> 4;

    }

    public int diff(int x, int y)
    {
        return Math.abs(x - y);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event){
        if(!enabled) return;
        if(ridingEntity == null)
        {
            return;
        }

        if(dupestate == 1)
        {
            if(diff(pchunkx, ridingEntity.getPosition().getX() >> 4) > 3 || diff(pchunkz, ridingEntity.getPosition().getZ() >> 4) > 3)
            {
                if(CancelPacketMod.enabled) {
                    CancelPacketMod.toggle();
                }

                dupestate = 2;
            }
            return;
        }
        if(dupestate == 3)
        {
            if(diff(pchunkx, ridingEntity.getPosition().getX() >> 4) > 4 || diff(pchunkz, ridingEntity.getPosition().getZ() >> 4) > 4) {
                if(ModifiedFreecam.enabled) {
                    ModifiedFreecam.onDisabled();
                }
                if(CancelPacketMod.enabled) {
                    CancelPacketMod.toggle();
                }
                enabled = false;
            }
            return;
        }
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.Incoming event) {
        if (!enabled) return;
        if (dupestate == 2) {
            if (event.getPacket() instanceof SPacketChunkData) {
                if (!CancelPacketMod.enabled) {
                    CancelPacketMod.toggle();
                }
                dupestate = 3;
            }
        }

    }

    @SubscribeEvent
    public void onDrawGameScreen(RenderGameOverlayEvent event) {

        if(!enabled || event.getType() != RenderGameOverlayEvent.ElementType.TEXT) {
            return;
        }

        gui.draw(-1, -1);

    }

}
