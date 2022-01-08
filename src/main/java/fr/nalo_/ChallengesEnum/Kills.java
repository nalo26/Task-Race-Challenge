package fr.nalo_.ChallengesEnum;

public enum Kills {
	Bat,
	Chicken,
	Cod,
	Cow,
	Horse,
	Pig,
	Piglin,
	Rabbit,
	Salmon,
	Sheep,
	Squid,
	Villager,
	Enderman,
	Llama,
	Spider,
	Blaze,
	Creeper,
	Drowned,
	Ghast,
	Hoglin,
	Magma_Cube,
	Skeleton,
	Wither_Skeleton,
	Zombie;
	
	public String toString() {
		return this.name().replace("_", " ");
	}
	
	public static String toEnumName(String name) {
		return name.replace(" ", "_").toUpperCase();
	}
}
