#!/usr/bin/env bash
# Servidor de teste local do Pangeia — ver ../testar-local.md
# Coloque este arquivo na mesma pasta do paper.jar.
set -euo pipefail

cd "$(dirname "$0")"

if [ ! -f paper.jar ]; then
  echo "paper.jar não encontrado nesta pasta."
  echo "Baixe o Paper 1.20.1 em https://papermc.io/downloads/paper e salve como paper.jar."
  exit 1
fi

java -Xms1G -Xmx2G -jar paper.jar nogui
