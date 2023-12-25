package jury.ezzerland.d2rbot.commands;

import jury.ezzerland.d2rbot.components.Responses;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static jury.ezzerland.d2rbot.TheJudge.BOT;

public class CmdRate {

    public CmdRate (SlashCommandInteractionEvent event) {
        Member member = event.getOption("tag").getAsMember();
        if (member == null) {
            event.reply(Responses.errorMessage("Member not found "+event.getOption("tag").getAsString())).setEphemeral(true).queue();
            return;
        }
        //BOT.getDatabase().updateRanking(member, 5, true);
        //BOT.getDatabase().updateRanking(member, 4, false);
        event.reply("Nothing Happened. Command not yet operational.").setEphemeral(true).queue();
    }
}
