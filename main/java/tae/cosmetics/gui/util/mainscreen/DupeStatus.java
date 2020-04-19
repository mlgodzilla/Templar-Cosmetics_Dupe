package tae.cosmetics.gui.util.mainscreen;

import tae.cosmetics.mods.Dupe;

import java.awt.*;

public class DupeStatus extends AbstractMoveableGuiList {

    private static int color = Color.LIGHT_GRAY.getRGB();

    public DupeStatus(String title, int startingX, int startingY) {
        super(title, startingX, startingY, color);
    }

    @Override
    protected void drawList() {
        int startY = y + fontRenderer.FONT_HEIGHT + 5;

        mc.fontRenderer.drawString(String.valueOf(Dupe.getDupestate()), x, startY + (fontRenderer.FONT_HEIGHT + 5), color);

        }
    }
