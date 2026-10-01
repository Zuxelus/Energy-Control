# IE specialized measurements in0.5

No new mod/dependency. Same approved official NeoForge1.21.1 IE12.4.2-194,
SHA256816685b898eed52080d08a4009042c511560ecc185601f21fe744936b63b5127.
The exact distribution/source archive were inspected privately; nothing from the
IE implementation or assets is copied into the GPL delivery.

- Current transformer (`current_transformer`): the public EnergyMeterBlockEntity
  getAveragePower method returns the recent real wire-transfer average. The
  upper dummy delegates to its master. Our adapter checks the actual class and
  invokes only that public zero-argument method; no private field access.
- Thermoelectric generator (`thermoelectric_generator`): public block-entity save
  data includes the upstream field `enegyOutput`. The spelling is intentional.
  This is potential generation for the current surrounding heat sources, not
  guaranteed energy delivered to a consumer. The screen explicitly says so.
- A separate common MachineProbe and Machine Data Card avoid conflating these
  machine rates with FE storage amount/capacity. Unsupported machines/platforms
  visibly report unavailable data. Fabric retains a no-support adapter.

Exact upstream source branch:
https://github.com/BluSunrize/ImmersiveEngineering/tree/1.21.1
Public sources used for signatures/schema were those in the exact release's
sources.jar from its official Maven source, not a guessed latest API.

Acceptance uses actual copper connections created via the public IE wire API,
actual LV connector FE input, a current transformer and actual sink capacitor.
The harness does not edit the meter sample buffer. Blue ice/contained lava are
placed/removed in the actual world. Two client views and clean restart are checked.
See QA-V5.md and runtime-v5/summary.json for final actual results; this plan alone
is not proof of acceptance. MV/HV, all wire types/loss details and all other IE
machines remain unaccepted. No claim about Fabric industrial compatibility.
