package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import jury.ezzerland.d2rbot.components.Run;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class CmdKick {

    public CmdKick(SlashCommandInteractionEvent event) {
        if (!BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.notInQueue()).setEphemeral(true).queue();
            return;
        }
        Run run = BOT.getParticipants().get(event.getMember());
        if (!run.getHost().equals(event.getMember())) {
            event.reply(Responses.notTheHost()).setEphemeral(true).queue();
            return;
        }
        if (event.getOption("uid") != null) {
            Member kicking = event.getGuild().getMemberById(event.getOption("uid").getAsString());
            if (kicking != null && run.getMembers().contains(kicking) && !run.getHost().equals(kicking)) {
                run.removeMember(kicking);
                event.reply(Responses.kickedPlayer(kicking.getEffectiveName())).setEphemeral(true).queue();
                return;
            }
        }
        event.replyEmbeds(Responses.kickMenu(run)).setEphemeral(true).queue();
    }

    public CmdKick (ButtonInteractionEvent event, String user) {
        if (!BOT.getParticipants().containsKey(event.getMember())) {
            event.reply(Responses.notInQueue()).setEphemeral(true).queue();
            return;
        }
        Run run = BOT.getParticipants().get(event.getMember());
        if (!run.getHost().equals(event.getMember())) {
            event.reply(Responses.notTheHost()).setEphemeral(true).queue();
            return;
        }
    }
}
