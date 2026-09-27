# Mistborn Dominance Tracker

An Android companion app for the Solo/Co-op mode of *Mistborn: The Deckbuilding Game*
(Brotherwise Games). It replaces the sliding Dominance Track card and the Lord Ruler's health
dial, and keeps the turn count, the Missions and a log of every game you finish.

The six tracks — Intro, Standard A, Standard B, Hard A, Hard B and Extreme — are transcribed
from Brotherwise's official
[Dominance Track Print and Play](https://www.brotherwisegames.com/s/Dominance-Track-Print-and-Play.pdf)
release, so you can play all of them without printing any cards.

## How to use it

### Start a game

Pick a difficulty under **New Game** on the main menu. The game screen has three parts:

- **The coins** at the top: current Dominance (the X value cards refer to), the Lord Ruler's
  health (starts at 48), and the turn.
- **The Dominance Track** in the middle, laid out like the physical card: rows already passed,
  the rows the latest Edict revealed (framed in gold), and the rows still to come. A tag such
  as **X=3** marks each row that raises Dominance.
- **The controls** at the bottom, within thumb reach.

### During play

| When… | Do this |
| --- | --- |
| An Edict raises Dominance | Tap **Bump**. For an Edict that raises it two or three times, tap **×2** or **×3** instead — one tap per Edict, never one per step. |
| You damage the Lord Ruler | Tap **−5** / **−1** under *Damage*. Reaching 0 wins the game. |
| A card heals him | Tap **+1** / **+5** under *Heal*. |
| A Mission tracker reaches its end | Tap its numeral (**I**, **II**, **III**). Tap again to undo. |
| A player's turn ends | Tap **Next Turn** (**−** steps back if you overshoot). |

Each Edict also heals the Lord Ruler automatically: **10 for every Mission not yet completed**
(30, 20, 10, or nothing once all three are done), never above 48. The caption under his coin
shows how much the next Edict will heal.

Past the track's final row, further Dominance shows as **+N additional Dominance** on the
latest Edict, as the final row's rule describes.

### Fixing mistakes

The latest Edict's frame has two buttons:

- **Undo bump** takes back that whole Edict — its Dominance and whatever it healed. Damage dealt
  since stays. Tap again to go further back.
- **Undo heal** keeps the Dominance but takes back the heal, for Edicts that don't heal the
  Lord Ruler.

Health, Missions and the turn can be corrected with their own buttons at any time.

### Ending a game

- Bringing the Lord Ruler to **0 health** ends the game in victory.
- **End game** (top right) asks whether you won — for instance through the Confrontation card —
  or lost.

Either way a **Congratulations** or **Game Over** summary appears. **Main Menu** goes back to
the menu; **Close** lets you look over the final state first (the controls give way to the
result, so nothing can change it by accident).
The game is saved to the log as soon as it ends.

**Quit** (or the back button) abandons a game in progress without saving it.

### Game log

Every finished game appears under **Game Log** on the main menu, newest first: difficulty,
result, turns, Dominance reached, Missions completed, the Lord Ruler's remaining health and
when it ended. The trash icon deletes an entry (after a confirmation) — handy for test games or
mistakes.

### Tablets

On a tablet in landscape, the whole track sits on the left with the coins and controls on the
right, so everyone around the table can read it.

## Install

The app isn't on the Play Store — you install it straight from this page. It works on Android
phones and tablets running Android 8 or newer (not on iPhone or iPad).

1. On your phone, open the **[latest release](https://github.com/timoneiro/mistborn-dominance-tracker/releases/latest)** and tap the file ending in
   **.apk** (for example `dominance-tracker-1.0.apk`) to download it.
2. When the download finishes, tap **Open** (or find the file in your **Downloads** / **My Files**
   app and tap it).
3. The first time, your phone will say it can't install apps from this source. Tap **Settings**,
   switch on **Allow from this source**, then go back and tap **Install**.
4. If a *Play Protect* message says the app is unknown, tap **More details → Install anyway**.
   This appears because the app isn't from the Play Store.
5. Tap **Open**, or find **Dominance Tracker** among your apps.

**Updating:** download the newer `.apk` from the latest release and install it the same way. Your
game log is kept.

## Development

- Build with `./gradlew assembleDebug` (the APK lands in `app/build/outputs/apk/debug/`), or
  install on a connected device with `./gradlew installDebug`.
- Game rules live in `data/GameState.kt` as an immutable value, with unit tests:
  `./gradlew testDebugUnitTest`
- The UI is Jetpack Compose (`ui/`); `ui/theme/` holds the palette, fonts and sizes.

## Contributing

Bug reports, ideas and pull requests are welcome. Fork the repository, make your change on a
branch, and open a pull request against `master`. Every pull request is reviewed by the
maintainer, and only the maintainer merges. See [CONTRIBUTING.md](CONTRIBUTING.md) for details.

## License

The source code is released under the [MIT License](LICENSE).

This is unofficial fan content, created and shared for non-commercial use. It has not been
reviewed by Dragonsteel Entertainment, LLC or Brotherwise Games, LLC.

*Mistborn* and *Mistborn: The Deckbuilding Game* belong to their respective owners, as does the
Dominance Track text in this app, transcribed from Brotherwise's Print and Play release. The MIT
License covers only the code written for this project.

The app bundles the [Cinzel](https://github.com/NDISCOVER/Cinzel) and
[Spectral](https://github.com/productiontype/Spectral) fonts, both under the SIL Open Font
License 1.1; their license texts ship with the app in `app/src/main/assets/licenses/`.
