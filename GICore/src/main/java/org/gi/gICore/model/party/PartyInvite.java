package org.gi.gICore.model.party;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PartyInvite {
    private UUID partyId;
    private UUID inviterId;
    private UUID invitedPlayerId;
    private LocalDateTime invitedAt;
    private LocalDateTime expiresAt;

    /**
     * 초대 생성 (기본 5분 만료)
     */
    public PartyInvite(UUID partyId, UUID inviterId, UUID invitedPlayerId) {
        this.partyId = partyId;
        this.inviterId = inviterId;
        this.invitedPlayerId = invitedPlayerId;
        this.invitedAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusMinutes(5);
    }

    /**
     * 초대가 만료되었는지 확인
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    @Override
    public String toString() {
        return "[PartyInvite] Party: %s, Inviter: %s, Invited: %s"
                .formatted(partyId, inviterId, invitedPlayerId);
    }
}
