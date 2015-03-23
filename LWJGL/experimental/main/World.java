package main;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL11.*;

public class World {
	
	public interface Bounds {

		int createDisplayList();
		
	}
	
	public static class CollisionResult {
		
		private Vector3f normal;
		@SuppressWarnings("unused")
		private float depth;
		
		public CollisionResult(Vector3f normal, float depth) {
			this.normal = normal;
			this.depth = depth;
		}
		
	}
	
	public enum CollisionDetector {
		
		AABBAABB {
			protected CollisionResult checkCollision(Bounds a, Vector3f p1, Bounds b, Vector3f p2) {
				Vector3f d1 = ((AABB) a).d, d2 = ((AABB) b).d;
				float ox = Math.min(p1.x + d1.x, p2.x + d2.x) - Math.max(p1.x, p2.x),
						oy = Math.min(p1.y + d1.y, p2.y + d2.y) - Math.max(p1.y, p2.y),
						oz = Math.min(p1.z + d1.z, p2.z + d2.z) - Math.max(p1.z, p2.z);
				if(ox <= 0 || oy <= 0 || oz <= 0) return null;
				float m = Math.min(ox, Math.min(oy, oz));
				Vector3f normal = null;
				if(m == ox) normal = new Vector3f(1, 0, 0);
				else if(m == oy) normal = new Vector3f(0, 1, 0);
				else if(m == oz) normal = new Vector3f(0, 0, 1);
				if(Vector3f.dot(Vector3f.sub(p2, p1, null), normal) < 0) normal.negate(normal);
				return new CollisionResult(normal, m);
			}
		};
		
		protected abstract CollisionResult checkCollision(Bounds a, Vector3f p1, Bounds b, Vector3f p2);
		
	}
	
	public class Entity {
		
		private float mass, i_mass;
		public Vector3f velocity, position;
		private Bounds bounds;
		private int list;
		
		public Entity(Bounds bounds, Vector3f position, Vector3f velocity, float mass, int list) {
			this.bounds = bounds;
			this.position = position;
			this.velocity = velocity;
			this.mass = mass;
			this.i_mass = mass == 0 ? 0 : 1 / mass;
			this.list = list == -1 ? bounds.createDisplayList() : list;
			entities.add(this);
		}
		
	}
	
	public void update(float delta) {
		for(int i = 0; i < entities.size(); i++) {
			Entity e1 = entities.get(i);
			Vector3f.add(e1.position, e1.velocity, e1.position);
			Vector3f.add(e1.velocity, (Vector3f) gravity.scale(e1.mass), e1.velocity);
			Vector3f v = Vector3f.sub((Vector3f) e1.velocity.scale(airDensity), e1.velocity, null);
			float d = Vector3f.dot(v, e1.velocity);
			e1.velocity = d < 0 ? new Vector3f(0, 0, 0) : v;
			if(e1.bounds == null) continue;
			for(int t = i + 1; t < entities.size(); t++) {
				Entity e2 = entities.get(t);
				if(e2.bounds == null) continue;
				for(CollisionDetector cd : CollisionDetector.values()) {
					try {
						CollisionResult r = cd.checkCollision(e1.bounds, e1.position, e2.bounds, e2.position);
						if(r != null) resolveCollision(e1, e2, r);
					} catch(ClassCastException e) {
						continue;
					}
				}
			}
		}
	}
	
	public void draw() {
		for(Entity entity : entities) {
			if(entity.list == 0) continue;
			glTranslatef(entity.position.x, entity.position.y, entity.position.z);
			glCallList(entity.list);
			glTranslatef(-entity.position.x, -entity.position.y, -entity.position.z);
		}
	}

	private List<Entity> entities = new ArrayList<Entity>();
	private Vector3f gravity;
	private float airDensity;
	
	public World(Vector3f gravity, float airDensity) {
		this.gravity = gravity;
		this.airDensity = airDensity;
	}
	
	private static void resolveCollision(Entity e1, Entity e2, CollisionResult r) {
		float tm = e1.i_mass + e2.i_mass, d = Vector3f.dot(Vector3f.sub(e2.velocity, e1.velocity, null), r.normal), e = 0, j = -(1 + e) * d / tm;
		Vector3f i = (Vector3f) r.normal.scale(j);
		Vector3f.sub(e1.velocity, (Vector3f) i.scale(e1.i_mass), e1.velocity);
		Vector3f.add(e2.velocity, (Vector3f) i.scale(e2.i_mass), e2.velocity);
	}
	
}
