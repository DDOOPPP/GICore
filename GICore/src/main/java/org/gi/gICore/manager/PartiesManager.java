package org.gi.gICore.manager;

import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyInvite;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import org.gi.gICore.GICore;
import org.gi.gICore.util.Result;
import org.gi.gICore.value.MessageName;

import java.util.List;
import java.util.UUID;

public class PartiesManager {
    private PartiesAPI partiesAPI;

    public PartiesManager() {
        this.partiesAPI = GICore.getPartiesAPI();
    }

    public boolean isSameParty(UUID player1Id, UUID player2Id) {
        return partiesAPI.areInTheSameParty(player1Id, player2Id);
    }

    public String getPartyName(UUID playerId) {
        if (partiesAPI.getPartyPlayer(playerId) == null) {
            return null;
        }
        return partiesAPI.getPartyPlayer(playerId).getPartyName();
    }

    public void ensurePartyDescription(String partyName) {
        var party = partiesAPI.getParty(partyName);

        if (party != null && party.getDescription() == null) {
            party.setDescription("new description");
        }
    }

    public List<Party> getOnlineParties() {
        return partiesAPI.getOnlineParties();
    }

    public Result createParty(UUID leaderId, String partyName) {
        var leaderData = partiesAPI.getPartyPlayer(leaderId);
        if (leaderData == null) return Result.ERROR(MessageName.PARTY_CREATED_FAIL);
        boolean result = partiesAPI.createParty(partyName, leaderData);
        return result ? Result.SUCCESS : Result.ERROR(MessageName.PARTY_CREATED_FAIL);
    }

    public Result isLeader(UUID playerId) {
        var leaderData = partiesAPI.getPartyPlayer(playerId);
        if (leaderData == null) return Result.ERROR(MessageName.NOT_JOIN_PARTY);

        Party party = partiesAPI.getParty(playerId);
        if (party == null) return Result.ERROR(MessageName.NOT_FOUND_PARTY);

        if (!playerId.equals(party.getLeader())) {
            return Result.ERROR(MessageName.NOT_PARTY_LEADER);
        }

        return Result.SUCCESS;
    }

    public Result removeParty(UUID leaderId) {
        Result result = isLeader(leaderId);
        if (!result.isSuccess()) {
            return result;
        }

        Party party = partiesAPI.getParty(leaderId);

        party.delete();
        return Result.SUCCESS;
    }

    public Result kickParty(UUID leaderId, UUID targetId) {
        Result result = isLeader(leaderId);
        if (!result.isSuccess()) {
            return result;
        }
        PartyPlayer target = partiesAPI.getPartyPlayer(targetId);

        Party party = partiesAPI.getParty(leaderId);
        return party.removeMember(target) ? Result.SUCCESS : Result.ERROR(MessageName.PARTY_KICKED_ERROR);
    }
}
