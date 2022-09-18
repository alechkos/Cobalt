package it.auties.whatsapp.listener;

import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.model.contact.Contact;
import it.auties.whatsapp.socket.Socket;

public interface OnWhatsappNewContact extends Listener {
    /**
     * Called when {@link Socket} receives a new contact
     *
     * @param whatsapp an instance to the calling api
     * @param contact the new contact
     */
    @Override
    void onNewContact(Whatsapp whatsapp, Contact contact);
}