# Contributing

Thanks for wanting to help! Bug reports, ideas and pull requests are all welcome.

## Reporting a bug or suggesting an idea

Open an [issue](https://github.com/timoneiro/mistborn-dominance-tracker/issues) and describe what
happened (or what you'd like to see). For bugs, say which difficulty you were playing and which
phone or tablet and Android version you use — a screenshot helps a lot.

## Proposing a change

1. **Fork** the repository, then create a branch in your fork from `master`, named after the
   change (for example `fix-undo-heal` or `add-haptic-feedback`).
2. Make your change, keeping each pull request to a single fix or feature.
3. Check that it builds and the tests pass:
   ```
   ./gradlew testDebugUnitTest assembleDebug
   ```
   If you changed a game rule (`data/GameState.kt`), add or update its tests. If you changed
   the UI, try it on a device or emulator and include screenshots in the pull request.
4. Open a **pull request** against `master` explaining what it changes and why.

For bigger changes — a new feature or a layout rework — please open an issue first, so we can
agree on the approach before you spend time on it.

## Review and merging

- Every pull request is reviewed by the maintainer, [@timoneiro](https://github.com/timoneiro).
- A pull request can only be merged with the maintainer's approval, and only the maintainer
  merges. Nobody else can push to `master` directly, force-push it or delete it.
- Expect questions or requests for changes. To update your pull request, push more commits to
  the same branch.

## Guidelines

- Match the style of the surrounding code (Kotlin's official code style).
- Game rules live in `GameState` as plain functions with unit tests; the ViewModel only stores
  the state and the UI only displays it.
- Track text is transcribed from Brotherwise Games' official Dominance Track Print and Play
  release. When correcting it, quote the card.
- Keep personal information — names, email addresses, file paths from your computer — out of
  the code.

## License

By contributing, you agree that your contributions are released under the project's
[MIT License](LICENSE).
