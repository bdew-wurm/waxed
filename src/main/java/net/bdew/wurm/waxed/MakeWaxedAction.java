package net.bdew.wurm.waxed;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.Items;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.skills.SkillList;
import org.gotti.wurmunlimited.modsupport.actions.*;

import java.util.Collections;
import java.util.List;

public class MakeWaxedAction implements ModAction, ActionPerformer, BehaviourProvider {
    private ActionEntry actionEntry;

    public MakeWaxedAction() {
        actionEntry = ActionEntry.createEntry((short) ModActions.getNextActionId(), "Preserve", "waxing", new int[]{
                1 /* ACTION_TYPE_NEED_FOOD */,
                4 /* ACTION_TYPE_FATIGUE */,
                6 /* ACTION_TYPE_NOMOVE */,
                48 /* ACTION_TYPE_ENEMY_ALWAYS */,
                36 /* ACTION_TYPE_ALWAYS_USE_ACTIVE_ITEM */
        });
        ModActions.registerAction(actionEntry);
    }

    @Override
    public short getActionId() {
        return actionEntry.getNumber();
    }

    @Override
    public BehaviourProvider getBehaviourProvider() {
        return this;
    }

    @Override
    public ActionPerformer getActionPerformer() {
        return this;
    }

    public boolean canUse(Creature performer, Item source, Item target) {
        return performer.isPlayer() && source != null && target != null &&
                source.getTemplateId() == ItemList.beeswax &&
                target.isFood() && !target.isLiquid()
                && source.getTopParent() == performer.getInventory().getWurmId()
                && target.getTopParent() == performer.getInventory().getWurmId()
                && !source.isTraded() && !target.isTraded() && !target.isNoDrop();
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item target) {
        if (canUse(performer, source, target))
            return Collections.singletonList(actionEntry);
        else
            return null;
    }

    @Override
    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
        if (!canUse(performer, source, target)) {
            performer.getCommunicator().sendAlertServerMessage("You can't do that now.");
            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }

        if (source.getWeightGrams() < target.getWeightGrams() / 10) {
            performer.getCommunicator().sendNormalServerMessage(String.format("You need at least %d grams of wax to preserve the %s.", target.getWeightGrams() / 10, target.getName()));
            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }

        if (counter == 1.0f) {
            performer.getCommunicator().sendNormalServerMessage(String.format("You start waxing %s to preserve it.", target.getNameWithGenus()));
            Server.getInstance().broadCastAction(String.format("%s starts waxing %s.", performer.getName(), target.getName()), performer, 5);
            action.setTimeLeft(Actions.getQuickActionTime(performer, performer.getSkills().getSkillOrLearn(SkillList.GROUP_COOKING), source, 0));
            performer.sendActionControl("waxing", true, action.getTimeLeft());
        } else if (counter * 10.0f > action.getTimeLeft()) {
            try {
                Item waxed = ItemFactory.createItem(WaxedItem.waxedItemId, target.getCurrentQualityLevel(), target.getRarity(), performer.getName());
                Item parent = target.getParent();
                waxed.setRealTemplate(target.getTemplateId());
                waxed.setName("waxed " + target.getName());
                waxed.setSizes(target.getSizeX(), target.getSizeY(), target.getSizeZ());
                waxed.setWeight(target.getWeightGrams(), false);
                waxed.setMaterial(target.getMaterial());
                source.setWeight(source.getWeightGrams() - target.getWeightGrams() / 10, true);
                parent.dropItem(target.getWurmId(), false);
                Items.destroyItem(target.getWurmId());
                parent.insertItem(waxed, true, false);
            } catch (FailedException | NoSuchTemplateException | NoSuchItemException e) {
                performer.getCommunicator().sendAlertServerMessage("Something went wrong, try again later or open a /support ticket.");
                WaxedMod.logException("Error waxing item", e);
                return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
            }
            performer.getCommunicator().sendNormalServerMessage(String.format("You finish waxing %s, it is no longer edible but won't spoil now. You used %d grams of wax.", target.getNameWithGenus(), target.getWeightGrams() / 10));
            Server.getInstance().broadCastAction(String.format("%s finishes waxing %s.", performer.getName(), target.getName()), performer, 5);
            return propagate(action, ActionPropagation.FINISH_ACTION, ActionPropagation.NO_SERVER_PROPAGATION, ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }

        return propagate(action, ActionPropagation.CONTINUE_ACTION);
    }
}
