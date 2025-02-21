package vulpesnova.VNContent.VNMobs;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class SnowyNightmareMobVN extends HostileMob {

    // Loaded in examplemod.ExampleMod.initResources()
    public static GameTexture texture;

    public static LootTable lootTable = new LootTable(new LootItemInterface[]
            {new ChanceLootItem(0.04F, "nightmareheadvn")}
    );

    // MUST HAVE an empty constructor
    public SnowyNightmareMobVN() {
        super(70);
        setSpeed(80);
        setFriction(3);

        // Hitbox, collision box, and select box (for hovering)
        collision = new Rectangle(-10, -7, 20, 14);
        hitBox = new Rectangle(-14, -12, 28, 24);
        selectBox = new Rectangle(-14, -7 - 34, 28, 48);
    }

    @Override
    public void init() {
        super.init();
        // Setup AI
        ai = new BehaviourTreeAI<>(this, new CollisionPlayerChaserWandererAI<>(null, 24 * 64, new GameDamage(50), 5, 40000));
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        // Spawn flesh debris particles
        for (int i = 0; i < 4; i++) {
            getLevel().entityManager.addParticle(new FleshParticle(
                    getLevel(), texture,
                    GameRandom.globalRandom.nextInt(5), // Randomize between the debris sprites
                    8,
                    32,
                    x, y, 20f, // Position
                    knockbackX, knockbackY // Basically start speed of the particles
            ), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        // Tile positions are basically level positions divided by 32. getTileX() does this for us etc.
        GameLight light = level.getLightLevel(getTileX(), getTileY());
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 51;

        // A helper method to get the sprite of the current animation/direction of this mob
        int dir = this.getDir();
        Point sprite = getAnimSprite(x, y, dir);

        drawY += getBobbing(x, y);
        drawY += getLevel().getTile(getTileX(), getTileY()).getMobSinkingAmount(this);

        DrawOptions drawOptions = texture.initDraw()
                .sprite(sprite.x, sprite.y, 64)
                .light(light)
                .pos(drawX, drawY);

        list.add(new MobDrawable() {
            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });

        addShadowDrawables(tileList, x, y, light, camera);
    }

    protected TextureDrawOptions getShadowDrawOptions(int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.human_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        drawY += this.getBobbing(x, y);
        drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount(this);
        return shadowTexture.initDraw().sprite(0, 0, res).light(light).pos(drawX, drawY);
    }

    @Override
    public int getRockSpeed() {
        // Change the speed at which this mobs animation plays
        return 20;
    }
    public boolean isLavaImmune() {
        return true;
    }

}
