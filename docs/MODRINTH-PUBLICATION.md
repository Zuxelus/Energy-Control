# Modrinth publication preparation

User authorized publication of an enhanced independent fork after current step2
(inventory cards, sensor kits, card holder, solid slopes) passes both loaders.
The user subsequently authorized GitHub publication through the connected GitHub app.
The app and official CLI both identify sdy623. A public fork has been created at
https://github.com/sdy623/Energy-Control-Enhanced with parent Zuxelus/Energy-Control.
GitHub source/release publication is separate from Modrinth submission.

Proposed name: Energy Control Enhanced. Independent fork of Zuxelus/Energy-Control,
GPL-3.0-only, Minecraft1.21.1 Fabric and NeoForge. Keep mod ID energycontrol for save
continuity; incompatible with simultaneously loading another Energy Control build.
Describe as incomplete beta, not full upstream parity. No Minecraft/dependency jars
or task fixtures belong in the downloadable production jar.

## Current actual publication blockers

No Modrinth connector or dedicated skill was found in the available tool catalog,
cloud/executor skills, or local .codex/.agents skill catalogs. Browser skills require
mcp__node_repl__js, which is absent. No account credentials were searched or exposed.
The verification-before-completion skill is used for build/delivery validation;
it does not provide a Modrinth login or upload capability.

Official content rules inspected2026-10-01:
https://modrinth.com/legal/rules (last modified2026-08-13).
Section4 permits license-abiding substantially divergent forks with proper credit.
Section6 requires AI disclosure and prohibits public projects primarily comprised
of AI output; AI-created gallery/icon images are prohibited. The newly ported
runtime code has been substantially authored by Codex. Preserving historical
source/assets does not by itself demonstrate eligibility of the new runtime.
Do not misrepresent authorship, omit disclosure, or treat a user's confirmation as
platform approval. Obtain a platform eligibility determination or human-led rework
before public submission. No publication, review request, or external message sent.

https://docs.modrinth.com/api/operations/createproject/
confirms authenticated project creation; no token has been supplied via a secure
handoff. A local release package and truthful draft can be prepared independently.

Step2 implementation acceptance is now complete locally:201 actual assertion
executions/58 original game frames; inventories, kits, holder, solid slopes,
main/offhand, multiplayer and normal-server reload. The buildable package is
1.21.1-0.6.0-port-beta (Energy Control Enhanced), not full upstream parity.
Modrinth submission remains unperformed; no approval outcome or project URL exists.
The authorized corresponding-source destination is the GitHub fork above. Release
assets include complete source/history archives split below20MB; verify the release
page and its checksums for upload completion. Modrinth remains unsubmitted.
