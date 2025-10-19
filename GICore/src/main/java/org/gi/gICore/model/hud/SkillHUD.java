package org.gi.gICore.model.hud;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.Indyuce.mmocore.skill.RegisteredSkill;
import net.Indyuce.mmocore.skill.binding.BoundSkillInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.gi.gICore.manager.UserManager;
import org.gi.gICore.util.TaskUtil;
import io.lumine.mythic.lib.player.cooldown.CooldownMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

public class SkillHUD {

    private boolean running = false;

    public void start() {
        if (running)
            return;
        running = true;

        // 0틱 후 10틱(0.5초)마다 반복
        TaskUtil.runSyncTimer(this::tick, 0L, 5L);
    }

    private void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline())
                continue;

            UUID uuid = player.getUniqueId();

            // “캐스팅 중인 유저만 HUD 표시” 조건
            if (!UserManager.getCasting(uuid))
                continue;

            PlayerData playerData = PlayerData.get(player);
            var boundSkillMap = playerData.getBoundSkills();

            if (boundSkillMap.isEmpty()) {
                player.sendActionBar(Component.text("등록된 스킬이 없습니다."));
                continue;
            }

            List<Component> components = new ArrayList<>();
            for (Entry<Integer, BoundSkillInfo> slot : boundSkillMap.entrySet()) {
                ClassSkill classSkill = slot.getValue().getClassSkill();
                RegisteredSkill  skill = classSkill.getSkill();
                if (skill == null)
                    continue;

                CooldownMap cooldownMap = playerData.getCooldownMap();
                String path = classSkill.getCooldownPath();

                Component slotComp = Component.text("[%d] ".formatted(slot.getKey()));
                Component nameComp = Component.translatable(skill.getName());

                // 쿨타임 체크 및 표시
                if (cooldownMap.isOnCooldown(path)) {
                    double remaining = cooldownMap.getCooldown(path);
                    Component coolTime = Component.text(" [%.1]".formatted(remaining));
                    components.add(slotComp.append(nameComp).append(coolTime));
                } else {
                    components.add(slotComp.append(nameComp));
                }
            }

            Component joined = Component.empty();
            for (int i = 0; i < components.size(); i++) {
                joined = joined.append(components.get(i));
                if (i < components.size() - 1)
                    joined = joined.append(Component.text("  "));
            }

            player.sendActionBar(joined);
        }
    }
}
