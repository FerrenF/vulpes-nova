package vulpesnova.VNContent.VNProjectiles;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
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

public class MagicForDummiesVNProjectile extends Projectile {
    private long spawnTime;
    private int sprite;

    public MagicForDummiesVNProjectile() {
    }

    public MagicForDummiesVNProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
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

    public void init() {
        super.init();
        this.height = 18.0F;
        this.piercing = 5;
        this.bouncing = 7;
        this.spawnTime = this.getLevel().getWorldEntity().getTime();
        this.givesLight = true;
        this.trailOffset = 0.0F;
        if (this.texture != null) {
            this.sprite = (new GameRandom((long)this.getUniqueID())).nextInt(this.texture.getWidth() / 64);
        }
    }

    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(215, 209, 225), 8.0F, 300, 18.0F);
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this);
            int textureRes = 64;
            int halfTextureRes = textureRes / 2;
            int drawX = camera.getDrawX(this.x) - halfTextureRes;
            int drawY = camera.getDrawY(this.y) - halfTextureRes;
            final TextureDrawOptions options = this.texture.initDraw().sprite(this.sprite, 0, textureRes).light(light).rotate(this.getAngle(), halfTextureRes, halfTextureRes).pos(drawX, drawY - (int)this.getHeight());
            list.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
            TextureDrawOptions shadowOptions = this.shadowTexture.initDraw().sprite(this.sprite, 100, textureRes).light(light).rotate(this.getAngle(), halfTextureRes, halfTextureRes).pos(drawX, drawY);
            tileList.add((tm) -> {
                shadowOptions.draw();
            });
        }
    }
}