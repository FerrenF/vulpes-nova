package vulpesnova.VNContent.VNMisc;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketPlayerGeneral;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.Level;

public class GEARResilienceMatrixItemVN extends ConsumableItem {
	
    public GEARResilienceMatrixItemVN() {
        super(5, true);
        this.rarity = Rarity.UNIQUE;
        this.worldDrawSize = 32;
    }


    @Override
    public boolean shouldSendToOtherClients(Level level, int x, int y, PlayerMob player, InventoryItem item,
			String error, GNDItemMap mapContent) {
        return error == null;
    }

   

    @Override
   	public void onOtherPlayerPlace(Level level, int x, int y, PlayerMob player, InventoryItem item,
   			GNDItemMap mapContent) {
        SoundManager.playSound(GameResources.shatter1, SoundEffect.effect(player));
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item,
			GNDItemMap mapContent) {
        player.setMaxResilience(Math.min(25, player.getMaxResilienceFlat() + 25));
        if (level.isServer()) {
            level.getServer().network.sendToAllClientsExcept(new PacketPlayerGeneral(player.getServerClient()), player.getServerClient());
        } else if (level.isClient()) {
            SoundManager.playSound(GameResources.shatter1, SoundEffect.effect(player));
        }

        if (this.singleUse) {
            item.setAmount(item.getAmount() - 1);
        }

        return item;
    }

    @Override
   	public String canPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent) {
        return player.getMaxResilienceFlat() >= 25 ? "incorrectresilience" : null;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "consumetip"));
        tooltips.add(Localization.translate("itemtooltip", "gearresiliencematrixvntip"));
        return tooltips;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.swingRotationInv(attackProgress);
    }
}
