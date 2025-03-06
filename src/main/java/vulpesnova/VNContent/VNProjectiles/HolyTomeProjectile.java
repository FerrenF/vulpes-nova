package vulpesnova.VNContent.VNProjectiles;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class HolyTomeProjectile extends FollowingProjectile {
    public HolyTomeProjectile() {
    }

    public HolyTomeProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
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
        this.turnSpeed = 0.75F;
        this.givesLight = true;
        this.height = 18.0F;
        this.trailOffset = -4.0F;
        this.setWidth(12.0F, true);
        this.piercing = 1;
        this.bouncing = 1;
    }
    
    @Override
    public Color getParticleColor() {
        return new Color(204, 192, 69);
    }
    
    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(204, 192, 69), 18.0F, 500, this.getHeight());
    }
    
    @Override
    public void updateTarget() {
        if (this.traveledDistance > 20.0F) {
            this.findTarget((m) -> {
                return m.isHostile;
            }, 160.0F, 350.0F);
        }

    }
    
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x) - 16;
            int drawY = camera.getDrawY(this.y);
            final TextureDrawOptions options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(drawX, drawY - (int)this.getHeight());
            list.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
        }
    }
}
