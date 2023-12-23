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
            case "game-judge-queue":
                new CmdInfo(event, user);
                break;
            case "rename-judge-queue":
                new CmdRename(event);
                break;
            case "runs-judge-queue":
                new CmdRuns(event);
                break;
            case "ladder-judge-queue":
                new CmdList(event, true, false, user);
                break;
            case "nonladder-judge-queue":
                new CmdList(event, false, false, user);
                break;
            case "hc-ladder-judge-queue":
                new CmdList(event, true, true, user);
                break;
            case "hc-nonladder-judge-queue":
                new CmdList(event, false, true, user);
                break;
            default:
                //unknown Button, do nothing
                break;
        }
    }
}
