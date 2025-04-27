#!/bin/bash
if [ $# -eq 0 ]; then
    echo "Por favor, proporciona un tag para la imagen como argumento."
    exit 1
else
    TAG=$1
fi
docker login
docker build -t josevictorgarcia/webapp11:$TAG -f docker/Dockerfile .
docker push josevictorgarcia/webapp11:$TAG
