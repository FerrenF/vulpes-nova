package vulpesnova.VNContent.VNProjectiles;

import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.level.maps.Level;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class SphereSentinelShotVNProjectile extends SpherecererShotVNProjectile {
	
    private static int SEARCH_RANGE = 600;
    public SphereSentinelShotVNProjectile() {
    	 this.sendPositionUpdate = true;
    	 this.maxMovePerTick = 15;
    }

    public SphereSentinelShotVNProjectile(Level level, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
    	super(level, x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }
    
    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(160, 90, 236), 8.0F, 500, 18.0F);
    }
    
    @Override
    public void onMoveTick(Point2D.Float startPos, double movedDist) {
		if (this.modifier != null) {
			this.modifier.onMoveTick(startPos, movedDist);
		}
		if(this.getAttackOwner() instanceof PlayerMob) return;
		ArrayList<PlayerMob> playersInArea = this.getLevel().entityManager.players.getInRegionRangeByTile(this.getTileX(), this.getTileY(), SEARCH_RANGE);	
    	if(playersInArea.size()==0) return;
    	
    	PlayerMob closest = playersInArea.get(0);   	 
    	Point2D targetPos = Projectile.getPredictedTargetPos(closest, this.x, this.y, this.speed, 3.0F);
        this.turnToward((float)targetPos.getX(), (float)targetPos.getY(), (float) (Math.PI / 2));
	}
}
