package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import jury.ezzerland.d2rbot.components.Run;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.regex.Pattern;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class CmdNextGame {
    public CmdNextGame(SlashCommandInteractionEvent event) {
        if (!BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.notInQueue()).setEphemeral(true).queue();
            return;
        }
        Run run = BOT.getParticipants().get(event.getMember());
        /*if (!run.getHost().equals(event.getMember())) {
            event.reply(Responses.notTheHost()).setEphemeral(true).queue();
            return;
        }*/
        if (run.renameIsOnCooldown()) {
            event.reply(Responses.renameCooldown()).setEphemeral(true).queue();
            return;
        }
        run.setGameName(increment(run.getGameName()));
        event.reply(Responses.renamedRun(run.getGameName())).addActionRow(Responses.broadcastButton(event.getMember().getId()), Responses.nextGameButton(event.getMember().getId())).setEphemeral(true).queue();
        BOT.getDatabase().addRun(run);
    }

    public CmdNextGame(ButtonInteractionEvent event) {
        if (!BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.notInQueue()).setEphemeral(true).queue();
            return;
        }
        Run run = BOT.getParticipants().get(event.getMember());
        /*if (!run.getHost().equals(event.getMember())) {
            event.reply(Responses.notTheHost()).setEphemeral(true).queue();
            return;
        }*/
        if (run.renameIsOnCooldown()) {
            event.reply(Responses.renameCooldown()).setEphemeral(true).queue();
            return;
        }
        run.setGameName(increment(run.getGameName()));
        event.reply(Responses.renamedRun(run.getGameName())).addActionRow(Responses.broadcastButton(event.getMember().getId()), Responses.nextGameButton(event.getMember().getId())).setEphemeral(true).queue();
        BOT.getDatabase().addRun(run);
    }


    static final Pattern NUMBER = Pattern.compile("\\d+$");

    static String increment(String input) {
        Responses.debug("");
        return NUMBER.matcher(input)
                .replaceFirst(s -> String.format(
                        "%0" + s.group().length() + "d",
                        Integer.parseInt(s.group()) + 1));
    }
}
