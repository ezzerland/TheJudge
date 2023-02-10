package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import jury.ezzerland.d2rbot.components.Run;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class CmdBroadcast {

    public CmdBroadcast(SlashCommandInteractionEvent event) {
        event.reply(broadcastCommand(event.getMember())).setEphemeral(true).queue();
    }

    public CmdBroadcast(ButtonInteractionEvent event) {
        event.reply(broadcastCommand(event.getMember())).setEphemeral(true).queue();
    }

    private String broadcastCommand (Member member) {
        if (!BOT.getParticipants().containsKey(member)) {
            return Responses.notInQueue();
        }
        Run run = BOT.getParticipants().get(member);
        run.broadcastRun(false);
        return Responses.announcementMade(run.getChannel().getId());
    }
}
