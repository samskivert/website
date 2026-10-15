#!/bin/sh

pandoc index.md -f markdown -t html5 -s --metadata title="Seeking Private Academic Tutor / Educator" -o index.html
