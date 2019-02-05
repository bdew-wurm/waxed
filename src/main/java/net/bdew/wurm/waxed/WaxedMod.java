package net.bdew.wurm.waxed;

import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WaxedMod implements WurmServerMod, PreInitable, Initable, ItemTemplatesCreatedListener, ServerStartedListener {
    private static final Logger logger = Logger.getLogger("Waxed");

    public static void logException(String msg, Throwable e) {
        if (logger != null)
            logger.log(Level.SEVERE, msg, e);
    }

    public static void logWarning(String msg) {
        if (logger != null)
            logger.log(Level.WARNING, msg);
    }

    public static void logInfo(String msg) {
        if (logger != null)
            logger.log(Level.INFO, msg);
    }

    @Override
    public void preInit() {
        ModActions.init();
    }

    @Override
    public void init() {
    }

    @Override
    public void onServerStarted() {
        ModActions.registerAction(new MakeWaxedAction());
        ModActions.registerActionPerformer(new WaxedExaminePerformer());
    }

    @Override
    public void onItemTemplatesCreated() {
        try {
            WaxedItem.register();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
