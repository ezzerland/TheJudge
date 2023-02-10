package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import jury.ezzerland.d2rbot.components.Run;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class CmdKickAll {

    public CmdKickAll(SlashCommandInteractionEvent event) {
        event.reply(kickAllCommand(event.getMember())).setEphemeral(true).queue();
    }

    public CmdKickAll (ButtonInteractionEvent event) {
        event.reply(kickAllCommand(event.getMember())).setEphemeral(true).queue();
    }

    private String kickAllCommand (Member member) {
        if (!BOT.getParticipants().containsKey(member)) {
            return Responses.notInQueue();
        }
        Run run = BOT.getParticipants().get(member);
        if (!run.getHost().equals(member)) {
            return Responses.notTheHost();
        }
        run.kickAllMembers();
        return Responses.kickedAll();
    }
}
