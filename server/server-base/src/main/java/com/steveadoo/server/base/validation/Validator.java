package com.steveadoo.server.base.validation;

import com.steveadoo.server.base.Player;

import java.util.concurrent.CompletableFuture;

public interface Validator {

    void init();

    /**
     * Checks if this validator uses this opcode for validation
     * @param clazz the class type to check
     * @return true if you want to use this message to validate the player
     */
    boolean matches(Class<?> clazz, Object message);

    /**
     * Starts validating the player using the message
     * @param player The player to validate
     * @param message The decoded message
     */
    CompletableFuture<Boolean> validate(Player player, Object message);

}
