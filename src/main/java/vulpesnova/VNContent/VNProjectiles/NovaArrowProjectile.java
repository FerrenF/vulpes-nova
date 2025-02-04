package vulpesnova.VNContent.VNProjectiles;

import necesse.engine.sound.SoundEffect;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class NovaArrowProjectile extends Projectile {
    public NovaArrowProjectile() {
    }

    public void init() {
        super.init();
        this.givesLight = true;
        this.height = 18.0F;
        this.heightBasedOnDistance = true;
        this.setWidth(8.0F);
    }

    public Color getParticleColor() {
        return new Color(153, 78, 234);
    }

    protected void modifySpinningParticle(ParticleOption particle) {
        particle.givesLight(75.0F, 0.5F).lifeTime(1000);
    }

    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(95, 36, 157), 12.0F, 250, this.getHeight());
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        if (this.getLevel().isServerLevel()) {
            if (mob != null) {
                ActiveBuff ab = new ActiveBuff("cosmicfirevn", mob, 10.0F, this.getOwner());
                mob.addBuff(ab, true);
            }

        }
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
            int drawY = camera.getDrawY(this.y);
            final TextureDrawOptions options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(drawX, drawY - (int)this.getHeight());
            list.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
            this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), 0);
        }
    }

    public void dropItem() {
        if (GameRandom.globalRandom.getChance(0.5F)) {
            this.getLevel().entityManager.pickups.add((new InventoryItem("goldarrow")).getPickupEntity(this.getLevel(), this.x, this.y));
        }

    }

    protected void playHitSound(float x, float y) {
        SoundManager.playSound(GameResources.bowhit, SoundEffect.effect(x, y));
    }
}
