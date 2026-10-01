# Actual bar depth regression

Initial Fabric screenshot graphics-face-east.png in fabric-graphics-before-bar-depth-fix
shows diagonal fill/background interference. Both quads shared the same depth.
Production PanelRenderer now places background at -0.02 and fill at -0.01 in
scaled row coordinates, behind text. The final fabric-graphics six-face shots
show solid fill with no observed stripes; their fractions continue to change.
Numerical assertions alone did not detect this issue; original images are retained.
