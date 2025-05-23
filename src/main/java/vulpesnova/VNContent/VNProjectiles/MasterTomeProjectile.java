package vulpesnova.VNContent.VNProjectiles;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class MasterTomeProjectile extends Projectile {


    public MasterTomeProjectile() {
    }

    public MasterTomeProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this();
    	this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
        this.setDamage(damage);
        this.knockback = knockback;
    }

    @Override
    public void init() {
        super.init();
        this.height = 18.0F;
        this.piercing = 3;
        this.bouncing = 5;
        this.givesLight = true;
        this.trailOffset = 0.0F;
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(138, 65, 227), 12.0F, 500, 18.0F);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            float alpha = this.getFadeAlphaTime(300, 200);
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
            int drawY = camera.getDrawY(this.y - this.getHeight()) - this.texture.getHeight() / 2;
            final TextureDrawOptions options = this.texture.initDraw().light(light.minLevelCopy(Math.min(light.getLevel() + 100.0F, 150.0F))).rotate(this.getAngle() - 0.0F, this.texture.getWidth() / 2, this.texture.getHeight() / 2).alpha(alpha).pos(drawX, drawY);
            list.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
        }
    }
}