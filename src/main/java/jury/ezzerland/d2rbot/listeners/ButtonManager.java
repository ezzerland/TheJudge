package jury.ezzerland.d2rbot.listeners;

import jury.ezzerland.d2rbot.commands.*;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class ButtonManager extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        BOT.cleanseRuns();
        String id = event.getButton().getId();
        String user = "";
        if (id.contains("judge") && id.contains(".")) {
            String[] split = id.split("[.]",0);
            id = split[0];
            user = split[split.length-1];
        }
        switch (id) {
            case "join-judge-queue":
                new CmdJoin(event, user);
                break;
            case "leave-judge-queue":
                new CmdLeave(event);
                break;
            case "broadcast-judge-queue":
                new CmdBroadcast(event);
                break;
            case "kick-judge-queue":
                new CmdKick(event, user);
                break;
            case "kickall-judge-queue":
                new CmdKickAll(event);
                break;
            case "nextgame-judge-queue":
                new CmdNextGame(event);
                break;
            case "info-judge-queue":
                new CmdInfo(event);
                break;
            case "rename-judge-queue":
                new CmdRename(event);
                break;
            case "ladder-judge-queue":
                new CmdList(event, true, user);
                break;
            case "nonladder-judge-queue":
                new CmdList(event, false, user);
                break;
            default:
                //unknown Button, do nothing
                break;
        }
    }
}
