package it.auties.whatsapp.model.chat;

import it.auties.protobuf.api.model.ProtobufMessage;
import it.auties.protobuf.api.model.ProtobufProperty;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.api.WhatsappListener;
import it.auties.whatsapp.model.contact.Contact;
import it.auties.whatsapp.model.contact.ContactJid;
import it.auties.whatsapp.model.contact.ContactStatus;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.sync.HistorySyncMessage;
import it.auties.whatsapp.util.Clock;
import it.auties.whatsapp.util.SortedMessageList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static it.auties.protobuf.api.model.ProtobufProperty.Type.*;

/**
 * A model class that represents a Chat.
 * A chat can be of two types: a conversation with a contact or a group.
 * This class is only a model, this means that changing its values will have no real effect on WhatsappWeb's servers.
 * Instead, methods inside {@link Whatsapp} should be used.
 * This class also offers a builder, accessible using {@link Chat#builder()}.
 */
@AllArgsConstructor
@Data
@Builder
@Jacksonized
@Accessors(fluent = true)
public class Chat implements ProtobufMessage {
    /**
     * The non-null unique jid used to identify this chat
     */
    @ProtobufProperty(index = 1, type = STRING,
            concreteType = ContactJid.class, required = true, requiresConversion = true)
    @NonNull
    private ContactJid jid;

    /**
     * A non-null arrayList of messages in this chat sorted chronologically
     */
    @ProtobufProperty(index = 2, type = MESSAGE, concreteType = HistorySyncMessage.class, repeated = true)
    @Default
    @NonNull
    private SortedMessageList messages = new SortedMessageList();

    /**
     * The nullable new unique jid for this Chat.
     * This field is not null when a contact changes phone number and connects their new phone number with Whatsapp.
     */
    @ProtobufProperty(index = 3, type = STRING,
            concreteType = ContactJid.class, requiresConversion = true)
    private ContactJid newJid;

    /**
     * The nullable old jid for this Chat.
     * This field is not null when a contact changes phone number and connects their new phone number with Whatsapp.
     */
    @ProtobufProperty(index = 4, type = STRING,
            concreteType = ContactJid.class, requiresConversion = true)
    private ContactJid oldJid;

    /**
     * The timestamp of the latest message in seconds since {@link java.time.Instant#EPOCH}
     */
    @ProtobufProperty(index = 5, type = UINT64)
    private long lastMessageTimestamp;

    /**
     * The number of unread messages in this chat.
     * If this field is negative, this chat is marked as unread.
     */
    @ProtobufProperty(index = 6, type = UINT32)
    private int unreadMessages;

    /**
     * This field is used to determine whether a chat is read only or not.
     * If true, it means that it's not possible to send messages here.
     * This is the case, for example, for groups where only admins can send messages.
     */
    @ProtobufProperty(index = 7, type = BOOLEAN)
    private boolean readOnly;

    /**
     * The endTimeStamp in seconds before a message is automatically deleted from this chat both locally and from WhatsappWeb's servers.
     * If ephemeral messages aren't enabled, this field has a value of 0
     */
    @ProtobufProperty(index = 9, type = UINT32)
    private long ephemeralMessageDuration;

    /**
     * The endTimeStamp in seconds since {@link java.time.Instant#EPOCH} when ephemeral messages were turned on.
     * If ephemeral messages aren't enabled, this field has a value of 0.
     */
    @ProtobufProperty(index = 10, type = INT64)
    private long ephemeralMessagesToggleTime;

    /**
     * The timestamp for the creation of this chat in seconds since {@link java.time.Instant#EPOCH}
     */
    @ProtobufProperty(index = 12, type = UINT64)
    private long timestamp;

    /**
     * The non-null display name of this chat
     */
    @ProtobufProperty(index = 13, type = STRING)
    private String name;

    /**
     * The hash of this chat
     */
    @ProtobufProperty(index = 14, type = STRING)
    private String hash;

    /**
     * This field is used to determine whether a chat was marked as being spam or not.
     */
    @ProtobufProperty(index = 15, type = BOOLEAN)
    private boolean notSpam;

    /**
     * This field is used to determine whether a chat is archived or not.
     */
    @ProtobufProperty(index = 16, type = BOOLEAN)
    private boolean archived;

    /**
     * The initiator of disappearing chats
     */
    @ProtobufProperty(index = 17, type = MESSAGE, concreteType = ChatDisappear.class)
    private ChatDisappear disappearInitiator;

    /**
     * The number of unread messages in this chat that have a mention to the user linked to this session.
     * If this field is negative, this chat is marked as unread.
     */
    @ProtobufProperty(index = 18, type = UINT32)
    private int unreadMentions;

    /**
     * Indicates whether this chat was manually marked as unread
     */
    @ProtobufProperty(index = 19, type = BOOLEAN)
    private boolean markedAsUnread;

    /**
     * If this chat is a group, this field is populated with the participants of this group
     */
    @ProtobufProperty(index = 20, type = MESSAGE,
            concreteType = GroupParticipant.class, repeated = true)
    private List<GroupParticipant> participants;

    /**
     * The token of this chat
     */
    @ProtobufProperty(index = 21, type = BYTES)
    private byte[] token;

    /**
     * The timestamp of the token of this chat
     */
    @ProtobufProperty(index = 22, type = UINT64)
    private long tokenTimestamp;

    /**
     * The public identity key of this
     */
    @ProtobufProperty(index = 23, type = BYTES)
    private byte[] identityKey;

    /**
     * The endTimeStamp in seconds since {@link java.time.Instant#EPOCH} when this chat was pinned to the top.
     * If the chat isn't pinned, this field has a value of 0.
     */
    @ProtobufProperty(index = 24, type = UINT32)
    private long pinned;

    /**
     * The mute status of this chat
     */
    @ProtobufProperty(index = 25, type = UINT64)
    @NonNull
    @Default
    private ChatMute mute = ChatMute.notMuted();

    /**
     * The wallpaper of this chat
     */
    @ProtobufProperty(index = 26, type = MESSAGE, concreteType = ChatWallpaper.class)
    private ChatWallpaper wallpaper;

    /**
     * The type of this media visibility set for this chat
     */
    @ProtobufProperty(index = 27, type = MESSAGE, concreteType = ChatMediaVisibility.class)
    @Default
    @NonNull
    private ChatMediaVisibility mediaVisibility = ChatMediaVisibility.DEFAULT;

    /**
     * The timestamp of the sender of the token of this chat
     */
    @ProtobufProperty(index = 28, type = UINT64)
    private long tokenSenderTimestamp;

    /**
     * Whether this chat was suspended and therefore cannot be accessed anymore
     */
    @ProtobufProperty(index = 29, type = BOOLEAN)
    private boolean suspended;

    /**
     * A map that holds the status of each participant, excluding yourself, for this chat.
     * If the chat is not a group, this map's size will range from 0 to 1.
     * Otherwise, it will range from 0 to the number of participants - 1.
     * It is important to remember that is not guaranteed that every participant will be present as a key.
     * In this case, if this chat is a group, it can be safely assumed that the user is not available.
     * Otherwise, it's recommended to use {@link Whatsapp#subscribeToContactPresence(Contact)} to force Whatsapp to send updates regarding the status of the other participant.
     * It's also possible to listen for updates to a contact's presence in a group or in a conversation by implementing {@link WhatsappListener#onContactPresence}.
     * The presence that this map indicates might not line up with {@link Contact#lastKnownPresence()} if the contact is composing, recording or paused.
     * This is because a contact can be online on Whatsapp and composing, recording or paused in a specific chat.
     */
    @Default
    private Map<Contact, ContactStatus> presences = new ConcurrentHashMap<>();

    /**
     * Returns the name of this chat
     *
     * @return a non-null string
     */
    public String name(){
        return Objects.requireNonNullElseGet(name,
                () -> this.name = jid.user());
    }

    /**
     * Returns a boolean to represent whether this chat is a group or not
     *
     * @return true if this chat is a group
     */
    public boolean isGroup() {
        return jid.type() == ContactJid.Type.GROUP;
    }

    /**
     * Returns a boolean to represent whether this chat is pinned or not
     *
     * @return true if this chat is pinned
     */
    public boolean isPinned() {
        return pinned != 0;
    }

    /**
     * Returns a boolean to represent whether ephemeral messages are enabled for this chat
     *
     * @return true if ephemeral messages are enabled for this chat
     */
    public boolean isEphemeral() {
        return ephemeralMessageDuration != 0 && ephemeralMessagesToggleTime != 0;
    }

    /**
     * Returns a boolean to represent whether this chat has a new jid
     *
     * @return true if this chat has a new jid
     */
    public boolean hasNewJid() {
        return newJid != null;
    }

    /**
     * Returns a boolean to represent whether this chat has unread messages
     *
     * @return true if this chat has unread messages
     */
    public boolean hasUnreadMessages() {
        return unreadMessages == 0 && unreadMentions == 0;
    }

    /**
     * Returns an optional value containing the new jid of this chat
     *
     * @return a non-empty optional if the new jid is not null
     */
    public Optional<ContactJid> newJid() {
        return Optional.ofNullable(newJid);
    }

    /**
     * Returns an optional value containing the old jid of this chat
     *
     * @return a non-empty optional if the old jid is not null
     */
    public Optional<ContactJid> oldJid() {
        return Optional.ofNullable(newJid);
    }

    /**
     * Returns an optional value containing the disappearing status of this chat
     *
     * @return a non-empty optional if the disappearing status of this chat is not null
     */
    public Optional<ChatDisappear> disappearInitiator() {
        return Optional.ofNullable(disappearInitiator);
    }

    /**
     * Returns an optional value containing the participants of this chat, if it is a group
     *
     * @return a non-empty optional if this chat is a group
     */
    public Optional<List<GroupParticipant>> participants() {
        return Optional.ofNullable(participants);
    }

    /**
     * Returns an optional value containing the wallpaper of this chat, if any is set
     *
     * @return a non-empty optional if this chat has a custom wallpaper
     */
    public Optional<ChatWallpaper> wallpaper() {
        return Optional.ofNullable(wallpaper);
    }

    /**
     * Returns an optional value containing the endTimeStamp this chat was pinned
     *
     * @return a non-empty optional if the chat is pinned
     */
    public Optional<ZonedDateTime> pinned() {
        return Clock.parse(pinned);
    }

    /**
     * Returns an optional value containing the endTimeStamp in seconds before a message is automatically deleted from this chat both locally and from WhatsappWeb's servers
     *
     * @return a non-empty optional if ephemeral messages are enabled for this chat
     */
    public Optional<ZonedDateTime> ephemeralMessageDuration() {
        return Clock.parse(ephemeralMessageDuration);
    }

    /**
     * Returns an optional value containing the endTimeStamp in seconds since {@link java.time.Instant#EPOCH} when ephemeral messages were turned on
     *
     * @return a non-empty optional if ephemeral messages are enabled for this chat
     */
    public Optional<ZonedDateTime> ephemeralMessagesToggleTime() {
        return Clock.parse(ephemeralMessagesToggleTime);
    }

    /**
     * Returns an optional value containing the latest message in chronological terms for this chat
     *
     * @return a non-empty optional if {@link Chat#messages} isn't empty
     */
    public Optional<MessageInfo> lastMessage() {
        return messages.isEmpty() ? Optional.empty() : Optional.of(messages.get(messages.size() - 1));
    }

    /**
     * Returns an optional value containing the first message in chronological terms for this chat
     *
     * @return a non-empty optional if {@link Chat#messages} isn't empty
     */
    public Optional<MessageInfo> firstMessage() {
        return messages.isEmpty() ? Optional.empty() : Optional.of(messages.get(0));
    }

    public static class ChatBuilder {
        public ChatBuilder messages(List<HistorySyncMessage> messages) {
            this.messages$value.addAll(new SortedMessageList(messages));
            return this;
        }

        public ChatBuilder participants(List<GroupParticipant> participants) {
            if (this.participants == null) this.participants = new ArrayList<>();
            this.participants.addAll(participants);
            return this;
        }
    }
}
