package vulpesnova.VNContent.VNMobs.VNPets;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.*;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerCollisionChaserAI;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class BabyTitanCubeMobVN extends AttackingFollowingMob {
    public static GameTexture texture;

    public int lifeTime = 4000;
    public GameDamage damage = new GameDamage(40.0F);
    public BabyTitanCubeMobVN() {
        super(10);
        this.setSpeed(40.0F);
        this.setFriction(2.0F);
        this.attackCooldown = 500;
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-13, -14, 26, 24);
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<AttackingFollowingMob>(this, new PlayerFollowerCollisionChaserAI<AttackingFollowingMob>(2000, this.damage, 30, 500, 640, 64));
    }

    @Override
    public void handleCollisionHit(Mob target, GameDamage damage, int knockback) {
        super.handleCollisionHit(target, damage, knockback);
        this.remove(0.0F, 0.0F, (Attacker) null, true);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.lifeTime -= 50;
        if (this.lifeTime <= 0) {
            this.remove(0.0F, 0.0F, (Attacker) null, true);
        }

    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for(int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), texture, 12, i, 16, this.x, this.y, 20.0F, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }

    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 15;
        int drawY = camera.getDrawY(y) - 22;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount(this);
        if (this.inLiquid(x, y)) {
            drawY -= 6;
        }

        final DrawOptions options = texture.initDraw().sprite(sprite.x, sprite.y, 32).light(light).pos(drawX, drawY);
        list.add(new MobDrawable() {
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });

        addShadowDrawables(tileList, x, y, light, camera);

    }

    @Override
    public int getRockSpeed() {
        return 20;
    }
}

