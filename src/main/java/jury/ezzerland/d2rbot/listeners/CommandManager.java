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
        commandData.add(Commands.slash("host", "Host a new run").addOptions(Responses.getRunTypeAsOption(), Responses.getLadderAsOption()));
        commandData.add(Commands.slash("list", "Lists the current runs available to join").addOptions(Responses.getRunTypeAsOption(), Responses.getLadderAsOption()));
        commandData.add(Commands.slash("runs", "Lists the amount of runs currently active"));
        commandData.add(Commands.slash("leave", "Leave the run you are currently in"));
        commandData.add(Commands.slash("rename", "Update the game name and password for your current run"));
        commandData.add(Commands.slash("kick", "Kick player by UID or list UID's of players in your run").addOptions(Responses.getKickOption()));
        commandData.add(Commands.slash("kickall", "Kick all players out of your current run, except you"));
        commandData.add(Commands.slash("broadcast", "Re-post the invite to join the run you are in"));
        commandData.add(Commands.slash("ng", "Automatically increment your game name run-001 to run-002 etc"));
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
                new CmdRuns(event);
                break;
            case "list":
                new CmdList(event);
                break;
            case "listall":
                new CmdListAll(event);
                break;
            case "leave":
                new CmdLeave(event);
                break;
            case "rename":
                new CmdRename(event);
                break;
            case "kick":
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
            default :
                //unknown command, do nothing
                break;
        }
    }
}
