package com.voxelwind.api.server.event.player;

import com.google.common.base.Preconditions;
import com.voxelwind.api.server.Player;
import com.voxelwind.api.server.event.Cancellable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * This event is fired when a player sends a message in chat.
 */
@ParametersAreNonnullByDefault
public class PlayerChatEvent implements PlayerEvent, Cancellable {
    private boolean cancelled;
    private final Player player;
    private String message;

    public PlayerChatEvent(Player player, String message) {
        this.player = Preconditions.checkNotNull(player, "player");
        this.message = Preconditions.checkNotNull(message, "message");
    }

    @Nonnull
    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the message that will be sent in the chat.
     * @return the chat message
     */
    @Nonnull
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message to display in chat.
     * @param message the chat message
     */
    public void setMessage(String message) {
        this.message = Preconditions.checkNotNull(message, "message");
    }

    /**
     * Set the cancellation state of the event. Set true if you want the player be disconnected.
     *
     * @param cancelled true to cancel the event.
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Gets the cancellation state of the event.
     *
     * @return whether the event has been canceled.
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}