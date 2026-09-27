package app.mistborndominance.data

/**
 * Content transcribed verbatim from Brotherwise Games' official "Dominance Track Print and
 * Play" release. Each track's physical front/back card pair is flattened into one continuous
 * list of real effect rows (the printed "flip this card" instruction is a print-medium artifact,
 * not a game effect, so it's omitted here).
 */
object Tracks {

    private val intro = Track(
        id = "intro",
        displayName = "Intro",
        maxDominance = 5,
        rows = listOf(
            TrackRow("No effect.", revealedDominance = 1),
            TrackRow("Each player Flares a metal or takes 3 damage."),
            TrackRow("Each player takes 3 damage."),
            TrackRow("The player with the most health takes 4 damage.", revealedDominance = 2),
            TrackRow("Each player must defeat one Ally of their choice."),
            TrackRow("The player with the least health takes 3 damage."),
            TrackRow("Each player may take 4 damage. If they do, they draw 2 cards."),
            TrackRow("Defeat each player's highest cost Ally.", revealedDominance = 3),
            TrackRow("Each player may Flare any number of metals, then take 1 damage for each unflared metal."),
            TrackRow("The player with the most health takes 6 damage."),
            TrackRow("Players take collective damage equal to the number of players multiplied by 4.", revealedDominance = 4),
            TrackRow("For each Ally they have, each player must either defeat the Ally or take 3 damage."),
            TrackRow("Each player takes 5 damage, then gains an Atium token."),
            TrackRow("The Mists Rise! Each player refreshes all metals.", revealedDominance = 5),
            TrackRow("Each player takes 7 damage. Additional Dominance does 10 damage to each player.", isOverflow = true),
        ),
    )

    private val standardA = Track(
        id = "standard_a",
        displayName = "Standard A",
        maxDominance = 6,
        rows = listOf(
            TrackRow("No effect.", revealedDominance = 1),
            TrackRow("Each player Flares a metal or takes 3 damage."),
            TrackRow("Each player takes 4 damage.", revealedDominance = 2),
            TrackRow("Each player must Flare a metal or defeat an Ally. Complete this effect twice."),
            TrackRow("The active player must Flare a metal or draw another Lord Ruler card."),
            TrackRow("Each player may take 3 damage. If they do, they may refresh up to 2 metals.", revealedDominance = 3),
            TrackRow("Each player gains 2 random cards from the market deck, keeps one and eliminates the other. They take damage equal to twice the cost of the kept card."),
            TrackRow("For each Adversary in front of a player they must Flare a metal or defeat an Ally.", revealedDominance = 4),
            TrackRow("Each player takes 3 damage for each of their Flared metals."),
            TrackRow("In turn order, each player must add an eliminated Funding card to the top of their deck or take 3 damage.", revealedDominance = 5),
            TrackRow("Each player draws 2 cards, then discards 2 cards."),
            TrackRow("The active player must Flare 2 metals or draw another Lord Ruler card.", revealedDominance = 6),
            TrackRow("Each player takes 3 damage for each of their Flared metals."),
            TrackRow("The Mists Rise! Each player refreshes all metals and may eliminate a card from their discard pile."),
            TrackRow("Each player takes 10 damage. Additional Dominance does 10 damage to each player.", isOverflow = true),
        ),
    )

    private val standardB = Track(
        id = "standard_b",
        displayName = "Standard B",
        maxDominance = 6,
        rows = listOf(
            TrackRow("No effect.", revealedDominance = 1),
            TrackRow("Each player Flares a metal or takes 3 damage."),
            TrackRow("Each player takes 4 damage.", revealedDominance = 2),
            TrackRow("Each player takes 3 damage for each Adversary in front of them."),
            TrackRow("Clear all black cubes on Adversary shields."),
            TrackRow("Each player may take 3 damage. If they do, they gain 2 boxings.", revealedDominance = 3),
            TrackRow("Each player defeats an Ally for each Adversary in front of them."),
            TrackRow("Defeat each player's highest cost Ally.", revealedDominance = 3),
            TrackRow("In turn order, each player must choose a defeated Adversary, if possible, and put it into play in front of them."),
            TrackRow("Each player takes 6 damage.", revealedDominance = 4),
            TrackRow("Each player takes 3 damage for each Adversary in front of them."),
            TrackRow("Each player draws 2 cards then discards 2 cards.", revealedDominance = 5),
            TrackRow("Clear all black cubes on Adversary shields.", revealedDominance = 6),
            TrackRow("The Mists Rise! Each player refreshes all metals and gains an Atium token."),
            TrackRow("Each player takes 5 damage. Additional Dominance does 10 damage to each player.", isOverflow = true),
        ),
    )

    private val hardA = Track(
        id = "hard_a",
        displayName = "Hard A",
        maxDominance = 8,
        rows = listOf(
            TrackRow("No effect.", revealedDominance = 1),
            TrackRow("Each player Flares a metal or takes 4 damage."),
            TrackRow("Each player takes 5 damage.", revealedDominance = 2),
            TrackRow("Each player must Flare a metal or defeat an Ally. Complete this effect twice."),
            TrackRow("The active player must Flare 2 metals or draw another Lord Ruler card.", revealedDominance = 3),
            TrackRow("Each player may take 6 damage. If they do, they may refresh up to 2 metals.", revealedDominance = 4),
            TrackRow("Each player gains 2 random cards from the market deck, then takes damage equal to twice the cost of the more expensive card."),
            TrackRow("For each Adversary a player has they must Flare a metal or defeat an Ally.", revealedDominance = 4),
            TrackRow("Each player takes 3 damage for each of their Flared metals.", revealedDominance = 5),
            TrackRow("In turn order, each player must add 2 eliminated Funding cards to the top of their deck, or take 6 damage."),
            TrackRow("Each player draws 1 card then discards 2 cards.", revealedDominance = 6),
            TrackRow("Each player must Flare 2 metals or discard the most expensive card in their hand."),
            TrackRow("Each player takes 3 damage for each of their Flared metals.", revealedDominance = 7),
            TrackRow("The Mists Rise! Each player refreshes all metals and may eliminate any number of cards in their hand, then draw back to 5."),
            TrackRow("Each player takes 10 damage. Additional Dominance does 10 damage to each player.", revealedDominance = 8, isOverflow = true),
        ),
    )

    private val hardB = Track(
        id = "hard_b",
        displayName = "Hard B",
        maxDominance = 7,
        rows = listOf(
            TrackRow("No effect.", revealedDominance = 1),
            TrackRow("Each player Flares a metal or takes 4 damage.", revealedDominance = 2),
            TrackRow("Each player takes 5 damage."),
            TrackRow("Each player takes 4 damage for each Adversary in front of them.", revealedDominance = 3),
            TrackRow("Clear all black cubes on Adversary shields."),
            TrackRow("Each player may take 6 damage. If they do, they gain 3 boxings.", revealedDominance = 4),
            TrackRow("Each player takes 3 damage and defeats an Ally for each Adversary in front of them."),
            TrackRow("In turn order, each player must choose a defeated Adversary, if possible, and put it into play in front of them.", revealedDominance = 4),
            TrackRow("Clear all black cubes on Adversary shields."),
            TrackRow("Defeat each player's highest cost Ally. Each player takes 6 damage.", revealedDominance = 5),
            TrackRow("Each player takes 4 damage for each Adversary's uncovered shield in front of them."),
            TrackRow("Each player draws 2 cards then discards 3 cards.", revealedDominance = 6),
            TrackRow("In turn order, each player must choose a defeated Adversary, if possible, and put it into play in front of them."),
            TrackRow("The Mists Rise! Each player refreshes all metals or defeats up to 2 Adversaries in front of them.", revealedDominance = 7),
            TrackRow("Each player takes 10 damage. Additional Dominance does 10 damage to each player.", isOverflow = true),
        ),
    )

    private val extreme = Track(
        id = "extreme",
        displayName = "Extreme",
        maxDominance = 10,
        rows = listOf(
            TrackRow("Each player takes 2 damage.", revealedDominance = 2),
            TrackRow("Each player takes 3 damage and must defeat an Ally of their choice.", revealedDominance = 3),
            TrackRow("Each player takes 3 damage."),
            TrackRow("Each player must take 4 damage and defeat an Ally of their choice.", revealedDominance = 4),
            TrackRow("Each player takes 4 damage."),
            TrackRow("Each player takes 5 damage.", revealedDominance = 5),
            TrackRow("Each player must take 5 damage and defeat an Ally of their choice."),
            TrackRow("Each player takes 6 damage.", revealedDominance = 6),
            TrackRow("Each player takes 6 damage."),
            TrackRow("Each player must take 7 damage and defeat an Ally of their choice.", revealedDominance = 7),
            TrackRow("Each player takes 7 damage."),
            TrackRow("Each player takes 8 damage.", revealedDominance = 8),
            TrackRow("Each player must take 8 damage and defeat an Ally of their choice."),
            TrackRow("The Mists Rise! All defeated players may come back to life with 16 health.", revealedDominance = 9),
            TrackRow("Each player takes 10 damage. Additional Dominance does 15 damage to each player.", revealedDominance = 10, isOverflow = true),
        ),
    )

    val all: List<Track> = listOf(intro, standardA, standardB, hardA, hardB, extreme)

    fun byId(id: String?): Track? = all.find { it.id == id }
}
