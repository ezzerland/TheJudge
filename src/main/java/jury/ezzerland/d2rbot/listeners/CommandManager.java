package jury.ezzerland.d2rbot.listeners;

import jury.ezzerland.d2rbot.commands.*;
import jury.ezzerland.d2rbot.components.Responses;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class CommandManager extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("host", "Host a new run").addOptions(Responses.getRunTypeAsOption(), Responses.getModeAsOption(), Responses.getRsvpAsOption(), Responses.getFlagAsOption()));
        //commandData.add(Commands.slash("list", "Lists the current runs available to join").addOptions(Responses.getRunTypeAsOption(), Responses.getLadderAsOption()));
        commandData.add(Commands.slash("runs", "Lists the amount of runs currently active"));
        commandData.add(Commands.slash("list", "Lists the amount of runs currently active"));
        commandData.add(Commands.slash("listall", "Lists ALL active runs"));
        commandData.add(Commands.slash("leave", "Leave the run you are currently in"));
        commandData.add(Commands.slash("end", "Leave the run you are currently in"));
        commandData.add(Commands.slash("rename", "Update the game name and password for your current run"));
        commandData.add(Commands.slash("kick", "List all players in your run with ability to kick"));
        commandData.add(Commands.slash("remove", "List all players in your run with ability to kick"));
        commandData.add(Commands.slash("kickall", "Kick all players out of your current run, except you"));
        commandData.add(Commands.slash("broadcast", "Re-post the invite to join the run you are in"));
        commandData.add(Commands.slash("ng", "Automatically increment your game name run-001 to run-002 etc"));
        commandData.add(Commands.slash("info", "List the information for the game you are currently in"));
        commandData.add(Commands.slash("add", "Add someone to the run you are currently in").addOptions(Responses.getAddOption()));
        commandData.add(Commands.slash("sethost", "Give host of your run to someone else").addOptions(Responses.getHostOption()));
        commandData.add(Commands.slash("rate", "Test Command").addOptions(Responses.getAddOption()));
        commandData.add(Commands.slash("leaderboard", "List the current leaderboard stats"));
        commandData.add(Commands.slash("stats", "See your own run stats"));
        event.getJDA().updateCommands().addCommands(commandData).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        BOT.cleanseRuns();
        switch (event.getName().toLowerCase()) {
            case "host":
                new CmdHost(event);
                break;
            case "runs":
            case "list":
                new CmdRuns(event);
                break;
            /*case "list":
                new CmdList(event);
                break;*/
            case "listall":
                new CmdListAll(event);
                break;
            case "leave":
            case "end":
                new CmdLeave(event);
                break;
            case "rename":
                new CmdRename(event);
                break;
            case "kick":
            case "remove":
                new CmdKick(event);
                break;
            case "kickall":
                new CmdKickAll(event);
                break;
            case "broadcast":
                new CmdBroadcast(event);
                break;
            case "ng":
                new CmdNextGame(event);
                break;
            case "info":
                new CmdInfo(event);
                break;
            case "add":
                new CmdAdd(event);
                break;
            case "sethost":
                new CmdSetHost(event);
                break;
            case "rate":
                new CmdRate(event);
                break;
            case "leaderboard":
                new CmdLeaderboard(event);
                break;
            case "stats":
                new CmdStats(event);
                break;
            default :
                //unknown command, do nothing
                break;
        }
    }
}
