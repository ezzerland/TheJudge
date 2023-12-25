package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import jury.ezzerland.d2rbot.components.Run;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class CmdRename {

    public CmdRename(SlashCommandInteractionEvent event) {
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
        event.replyModal(Responses.getGameInfoModal(run.getGameName(), run.getPassword(), Integer.toString(run.getMaxMembers()), run.getDescription(), false)).queue();
    }

    public CmdRename(ButtonInteractionEvent event) {
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
        event.replyModal(Responses.getGameInfoModal(run.getGameName(), run.getPassword(), Integer.toString(run.getMaxMembers()), run.getDescription(), false)).queue();
    }

}
