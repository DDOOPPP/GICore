package org.gi.gICore.model.party;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@AllArgsConstructor
public class Party {
    private UUID partyId;
    private String partyName;
    @Setter
    private UUID leaderId;
    private Set<UUID> members;
    private LocalDateTime createdAt;
    @Setter
    private int maxMembers;

    /**
     * 새 파티 생성
     */
    public Party(String partyName, UUID leaderId) {
        this.partyId = UUID.randomUUID();
        this.partyName = partyName;
        this.leaderId = leaderId;
        this.members = new HashSet<>();
        this.members.add(leaderId);
        this.createdAt = LocalDateTime.now();
        this.maxMembers = 5; // 기본 최대 인원
    }

    /**
     * 파티원 추가
     */
    public boolean addMember(UUID playerId) {
        if (isFull()) {
            return false;
        }
        return members.add(playerId);
    }

    /**
     * 파티원 제거
     */
    public boolean removeMember(UUID playerId) {
        return members.remove(playerId);
    }

    /**
     * 파티가 가득 찼는지 확인
     */
    public boolean isFull() {
        return members.size() >= maxMembers;
    }

    /**
     * 플레이어가 파티원인지 확인
     */
    public boolean isMember(UUID playerId) {
        return members.contains(playerId);
    }

    /**
     * 플레이어가 파티장인지 확인
     */
    public boolean isLeader(UUID playerId) {
        return leaderId.equals(playerId);
    }

    /**
     * 파티원 수 반환
     */
    public int getMemberCount() {
        return members.size();
    }

    @Override
    public String toString() {
        return "[Party_Id: %s] [PartyName: %s] [LeaderId: %s] [Members: %d/%d]"
                .formatted(partyId, partyName, leaderId, members.size(), maxMembers);
    }
}
