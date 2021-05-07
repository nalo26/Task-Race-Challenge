package fr.nalo_.ChallengesEnum;

public enum Deaths {
	CONTACT("<player> was pricked to death"),
	DROWNING("<player> drowned"),
	BLOCK_EXPLOSION("<player> was killed by [Intentional Game Design]"),
	FALL("<player> fell from a high place"),
	FALLING_BLOCK("<player> was squashed by a falling block"),
	FIRE_TICK("<player> burned to death"),
	LAVA("<player> tried to swim in lava"),
	ENTITY_ATTACK("<player> was slain by <mob>"),
	PROJECTILE("<player> was shot by <mob>"),
	STARVATION("<player> starved to death"),
	SUFFOCATION("<player> suffocated in a wall");
	
	private String description;

	Deaths(String desc) {
		this.description = desc;
	}

	public String getDescription() {
		return this.description;
	}
	
	public static String getFromDescription(String desc) {
		for(Deaths d : Deaths.values()) {
			if(d.description.equals(desc)) return d.name();
		}
		
		return null; // Should never happen.
	}

}
