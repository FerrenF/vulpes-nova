package vulpesnova.VNContent.VNWeapons.VNRange.VNAmmos;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.bulletItem.BulletItem;
import vulpesnova.VNContent.VNProjectiles.NovaBulletProjectile;

public class NovaBulletItem extends BulletItem {
    public NovaBulletItem() {
        super(1000);
        this.damage = 11;
        this.rarity = Rarity.UNCOMMON;
        this.setItemCategory("equipment", "ammo");
    }
    
    @Override
    public boolean overrideProjectile() {
        return true;
    }

    @Override
    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, Mob owner) {
        return new NovaBulletProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, owner);
    }
    
    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "novabulletvntip"));
        return tooltips;
    }
}
