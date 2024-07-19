package ru.y_lab.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing different types of resources in a coworking service.
 */
@RequiredArgsConstructor
@Getter
public enum ResourceType {

    /**
     * Conference room for meetings and presentations.
     */
    CONFERENCE_ROOM("Conference Room"),

    /**
     * Private office for individual work.
     */
    PRIVATE_OFFICE("Private Office"),

    /**
     * Shared desk in an open workspace.
     */
    SHARED_DESK("Shared Desk"),

    /**
     * Dedicated desk for exclusive use.
     */
    DEDICATED_DESK("Dedicated Desk"),

    /**
     * Lounge area for relaxation and informal meetings.
     */
    LOUNGE_AREA("Lounge Area"),

    /**
     * Event space for hosting events and workshops.
     */
    EVENT_SPACE("Event Space"),

    /**
     * Phone booth for private calls.
     */
    PHONE_BOOTH("Phone Booth"),

    /**
     * Virtual office for remote work services.
     */
    VIRTUAL_OFFICE("Virtual Office");

    private final String displayName;
}
