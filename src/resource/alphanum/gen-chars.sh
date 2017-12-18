#!/bin/bash

for c in "-" {0..9} {A..Z}; do

cat << EOF > "$c.svg"
<svg height="18" width="13">
  <text x="0" y="17" fill="black" font-family="Monospace" font-size="22">$c</text>
</svg>
EOF

inkscape -f "$c.svg" -e "$(echo ${c,,}).png"

rm -- "$c.svg"

done
