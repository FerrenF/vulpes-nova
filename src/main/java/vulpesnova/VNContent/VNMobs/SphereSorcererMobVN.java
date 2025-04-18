package vulpesnova.VNContent.VNMobs;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.packet.PacketMobFollowUpdate;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.*;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserWandererAI;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.WillOWispMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import vulpesnova.VNContent.VNProjectiles.SpherecererShotVNProjectile;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.function.Supplier;

public class SphereSorcererMobVN extends HostileMob {

    public static GameTexture texture;
    public static GameDamage damage;
    static {
        damage = new GameDamage(30.0F);
    }

    public static LootTable lootTable = new LootTable(
            ChanceLootItem.between(0.4f, "shapeshardsvn", 1, 3)
    );

    public SphereSorcererMobVN() {
        super(200);     
        this.setSpeed(20);
        this.setFriction(3.0F);
        this.setKnockbackModifier(3.0F);
        this.setArmor(10);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
    }

    
    @Override
    public boolean isValidSpawnLocation(Server server, ServerClient client, int targetX, int targetY) {
        MobSpawnLocation location = (new MobSpawnLocation(this, targetX, targetY))
        		.checkMobSpawnLocation()
        		.checkMaxHostilesAround(3, 10, client);
        
        if (this.getLevel().isCave) {
            location = location.checkLightThreshold(client);
        } else {
            location = location.checkMaxStaticLightThreshold(10);
        }

        return location.validAndApply();
    }

    @Override
    public void init() {
    	
        super.init();
        
        this.ai = new BehaviourTreeAI<Mob>(this, new PlayerChaserWandererAI<Mob>((Supplier<Boolean>)null, 600, 320, 40000, false, false) {
            public boolean attackTarget(Mob mob, Mob target) {
            	
            	Projectile p = this.shootAndGetSimpleProjectile(mob, target, "spherecererproj", damage, 90, 450, 1);            	
            	return (p!=null);
            }
            
            public Projectile shootAndGetSimpleProjectile(Mob mob, Mob target, String projectileID, GameDamage damage, int speed,
        			int distance, int moveDist) {
        		if (mob.canAttack()) {
        			mob.attack(target.getX(), target.getY(), false);
        			
        			Point2D.Float predictor = Projectile.getPredictedTargetPos(target, SphereSorcererMobVN.this.x, SphereSorcererMobVN.this.y, speed, Math.max(1, target.getCurrentSpeed() / 10));
        			SpherecererShotVNProjectile projectile = (SpherecererShotVNProjectile)ProjectileRegistry.getProjectile(projectileID, mob.getLevel(), mob.x, mob.y,
        					predictor.x, predictor.y, (float) speed, distance, damage, mob);
        			projectile.moveDist((double) moveDist);
        			mob.getLevel().entityManager.projectiles.add(projectile);
        			return projectile;
        		} else {
        			return null;
        		}
        	}
        });
    }
    
    protected boolean nightSwitch = false;

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isAttacking) {
            this.getAttackAnimProgress();
        }
      
    }

    
    private void runWispSpawnCheck() {
    	 if((!this.getLevel().isCave) && this.getLevel().getWorldEntity().isNight()) {  	
         	if(!nightSwitch) {
         		nightSwitch = true;
         		this.onNightTime();
         		
         	}
         }
         else {
         	if(nightSwitch) {
         		nightSwitch = false;
         		this.onDayTime();
         	}
         }
    }
        
    protected WillOWispMob myWisp;
    private void onDayTime() {
		if(myWisp != null) {
			myWisp.remove();
			myWisp = null;
		}
		
	}


	private void onNightTime() {
		if(myWisp == null) {		
			
				myWisp = new WillOWispMob();
				Point spawnPoint = new Point(getX() + GameRandom.globalRandom.getIntBetween(-5, 5),
						getY() + GameRandom.globalRandom.getIntBetween(-5, 5));
				this.getLevel().entityManager.addMob(myWisp, (float) spawnPoint.x, (float) spawnPoint.y);
				myWisp.setFollowing(this, false);
				this.getLevel().getServer().network.sendToClientsWithEntity(
						new PacketMobFollowUpdate(this.getUniqueID(), this.followingUniqueID), this);
			}
		
		
	}

	@Override
	public void remove(float knockbackX, float knockbackY, Attacker attacker, boolean isDeath) {
		super.remove(knockbackX, knockbackY, attacker, isDeath);
		if(myWisp!=null) {
			myWisp.remove();
			myWisp = null;
		}
	}
	@Override
    public void serverTick() {
        super.serverTick();
        if (this.isAttacking) {
            this.getAttackAnimProgress();
        }
        runWispSpawnCheck();
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        // Spawn flesh debris particles
        for (int i = 0; i < 3; i++) {
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
        int drawY = camera.getDrawY(y) - 43;

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

}
