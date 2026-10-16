#!/bin/sh

cd `dirname $0`
pandoc index.md -f markdown -t html5 -s \
  --metadata title="Bayne Household" \
  --metadata subtitle="Educating our children since 2011" \
  -o index.html
