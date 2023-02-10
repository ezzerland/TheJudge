package jury.ezzerland.d2rbot.listeners;

import jury.ezzerland.d2rbot.commands.CmdHost;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ModalManager extends ListenerAdapter {
    @Override
    public void onModalInteraction(ModalInteractionEvent event) {

        switch (event.getModalId()) {
            case "host-true":
                new CmdHost(event, true);
                break;
            case "host-false":
                new CmdHost(event, false);
                break;
            default :
                //unknown modal, do nothing
                break;
        }
    }
}
