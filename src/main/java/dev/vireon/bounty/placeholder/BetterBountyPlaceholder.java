
package dev.vireon.bounty.placeholder;

import dev.vireon.bounty.BountyPlugin;
import dev.vireon.bounty.bounty.Bounty;
import dev.vireon.bounty.bounty.BountyIndex;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BetterBountyPlaceholder extends PlaceholderExpansion {

    private final BountyPlugin plugin;

    public BetterBountyPlaceholder(BountyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "betterbounty";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    private Bounty getCurrentHudBounty() {

        List<Bounty> bounties = new ArrayList<>(
                plugin.getBountyManager().getAllBounties(
                        BountyIndex.SortField.AMOUNT
                )
        );

        bounties.removeIf(bounty ->
                bounty == null || bounty.getAmount() <= 0
        );

        if (bounties.isEmpty()) {
            return null;
        }

        long period = System.currentTimeMillis() / 5000L;
        int index = (int) (period % bounties.size());

        return bounties.get(index);
    }

    @Override
    public @Nullable String onRequest(
            OfflinePlayer player,
            @NotNull String params
    ) {

        if (params.equalsIgnoreCase("amount")) {

            if (player == null) {
                return "0";
            }

            Bounty bounty = plugin.getBountyManager()
                    .getBounty(player.getUniqueId());

            return bounty == null
                    ? "0"
                    : String.valueOf(bounty.getAmount());
        }

        if (params.equalsIgnoreCase("hud_name")
                || params.equalsIgnoreCase("hud_amount")
                || params.equalsIgnoreCase("hud_uuid")) {

            Bounty bounty = getCurrentHudBounty();

            if (bounty == null) {
                if (params.equalsIgnoreCase("hud_name")) {
                    return "Aranan yok";
                }
                if (params.equalsIgnoreCase("hud_amount")) {
                    return "0";
                }
                return "";
            }

            if (params.equalsIgnoreCase("hud_name")) {
                String name = bounty.getPlayerName();
                return name == null ? "Bilinmeyen" : name;
            }

            if (params.equalsIgnoreCase("hud_amount")) {
                return String.valueOf(bounty.getAmount());
            }

            return bounty.getPlayerId().toString();
        }

        return null;
    }
}
