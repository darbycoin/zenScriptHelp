package ZenTester;

import Zen.*;

import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;

import java.awt.*;

@ScriptManifest(category = Category.MISC, name = "ZenTester", author = "Zenarchist", version = 1.0, description = "For testing")
public class Main extends AbstractScript {
    // Declare anti-ban instance
    private Zen z;
    private int START_X;
    private int START_Y;

    @Override
    public void onStart() {
        // Construct the Zen helper class and attach it to this script
        z = new Zen(this);
        // Wait until the user is logged in before building the GUI as it depends on character data
        while (!getClient().isLoggedIn())
            z.setStatus("#Logged out");
        // Setup GUI
        z.gui.addStringInput(0, "Starting Coords (x,y)", "location", "Leave blank to use current location", "");
        if (!z.showGUI())
            return;
        // Add starting items to don't-drop list if the option is selected
        z.saveStartingItems();

        // Set up antiban
        if (!z.gui.getString("chatflags").equals(""))
            z.ban.setChatFlags(z.gui.getString("chatflags").split(","));

        for(String s : z.gui.getString("dontdrop").split(","))
            z.dontDrop(s);

        if(z.gui.getStrings("location") == null) {
            START_X = getLocalPlayer().getX();
            START_Y = getLocalPlayer().getY();
        } else {
            START_X = Integer.parseInt(z.gui.getStrings("location")[0]);
            START_Y = Integer.parseInt(z.gui.getStrings("location")[1]);
        }
    }

    @Override
    public int onLoop() {
        // Check for random flag (for adding extra customized anti-ban features)
        if(z.getAntiBan().doRandom())
            log("Script-specific random flag triggered");

        // Call anti-ban (returns a wait time after performing any actions)
        return z.getAntiBan().antiBan();
    }

    @Override
    // Draw anti-ban info to the screen
    public void onPaint(Graphics g) {
        g.drawString("Anti-Ban Status: " + (z.getAntiBan().getStatus().equals("") ? "Inactive" : z.getAntiBan().getStatus()), 10, 100);
        g.drawString("Starting Position: (" + START_X + ", " + START_Y + ")", 10, 100);
    }
}
