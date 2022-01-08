package fr.nalo_.ChallengesEnum;

public enum Advancements {
	MINE_STONE("Stone Age", "story"),
	UPGRADE_TOOLS("Getting an Upgrade", "story"),
	SMELT_IRON("Acquire Hardware", "story"),
	OBTAIN_ARMOR("Suit Up", "story"),
	LAVA_BUCKET("Hot Stuff", "story"),
	IRON_TOOLS("Isn't It Iron Pick", "story"),
	DEFLECT_ARROW("Not Today, Thank You", "story"),
	FORM_OBSIDIAN("Ice Bucket Challenge", "story"),
	MINE_DIAMOND("Diamonds!", "story"),
	ENTER_THE_NETHER("We Need to Go Deeper", "story"),
	SHINY_GEAR("Cover Me With Diamonds", "story"),
	ENCHANT_ITEM("Enchanter", "story"),
	RETURN_TO_SENDER("Return to Sender", "nether"),
	FIND_BASTION("Those Were the Days", "nether"),
	FIND_FORTRESS("A Terrible Fortress", "nether"),
	DISTRACT_PIGLIN("Oh Shiny", "nether"),
	LOOT_BASTION("War Pigs", "nether"),
	OBTAIN_BLAZE_ROD("Into Fire", "nether"),
	BREW_POTION("Local Brewery", "nether"),
	KILL_A_MOB("Monster Hunter", "adventure"),
	TRADE("What a Deal!", "adventure"),
	SLEEP_IN_BED("Sweet Dreams", "adventure"),
	SHOOT_ARROW("Take Aim", "adventure"),
	SUMMON_IRON_GOLEM("Hired Help", "adventure"),
	SNIPER_DUEL("Sniper Duel", "adventure"),
	BREED_AN_ANIMAL("The Parrots and the Bats", "husbandry"),
	TAME_AN_ANIMAL("Best Friends Forever", "husbandry"),
	FISHY_BUSINESS("Fishy Business", "husbandry"),
	PLANT_SEED("A Seedy Place", "husbandry"),
	TACTICAL_FISHING("Tactical Fishing", "husbandry");

	private String description;
	private String categorie;

	Advancements(String desc, String categ) {
		this.description = desc;
		this.categorie = categ;
	}

	public String getDescription() {
		return this.description;
	}

	public String getCategorie() {
		return this.categorie;
	}
	
	public String toString() {
		return this.categorie + "/" + this.name().toLowerCase();
	}
	
	public static String getFromDescription(String desc) {
		for(Advancements a : Advancements.values()) {
			if(a.description.equals(desc)) return a.toString();
		}
		
		return null; // Should never happen.
	}

}
