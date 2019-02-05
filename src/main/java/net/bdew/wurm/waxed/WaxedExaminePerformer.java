package net.bdew.wurm.waxed;

import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.behaviours.MethodsItems;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.players.PlayerInfoFactory;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;

public class WaxedExaminePerformer implements ActionPerformer {
    @Override
    public short getActionId() {
        return Actions.EXAMINE;
    }

    @Override
    public boolean action(Action action, Creature performer, Item target, short num, float counter) {
        if (target.getTemplateId() == WaxedItem.waxedItemId && target.getRealTemplate() != null) {
            String res = target.getRealTemplate().getDescriptionLong();

            if (target.creator != null && target.creator.length() > 0) {
                res += String.format(" It was waxed by %s on %s", target.creator, WurmCalendar.getDateFor(target.creationDate));
            }

            if (target.isPlanted()) {
                PlayerInfo pInfo = PlayerInfoFactory.getPlayerInfoWithWurmId(target.lastOwner);
                String planter = "someone";
                if (pInfo != null) {
                    planter = pInfo.getName();
                }
                res += " It has been firmly secured to the ground by " + planter + ".";
            }

            if (target.getRarity() > 0) {
                res += MethodsItems.getRarityDesc(target.rarity);
            }

            if (target.getColor() != -1) {
                res += MethodsItems.getColorDesc(target.getColor());
            }

            performer.getCommunicator().sendNormalServerMessage(res);

            for (final String s : MethodsItems.getEnhancementStrings(target)) {
                performer.getCommunicator().sendNormalServerMessage(s);
            }

            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        } else {
            return propagate(action, ActionPropagation.CONTINUE_ACTION, ActionPropagation.SERVER_PROPAGATION, ActionPropagation.ACTION_PERFORMER_PROPAGATION);
        }

    }
}
