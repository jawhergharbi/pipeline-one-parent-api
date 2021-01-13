#!/bin/bash
echo "GOOGLE_APPLICATION_CREDENTIALS: " $GOOGLE_APPLICATION_CREDENTIALS
sudo docker run \
  -p 8080:8080 pipeline-api:0.0.01 \
  -e GOOGLE_APPLICATION_CREDENTIALS=/tmp/keys/pipeline-api-dev-1bd92820d1bd.json \
  -v $GOOGLE_APPLICATION_CREDENTIALS:/tmp/keys/pipeline-api-dev-1bd92820d1bd.json:ro

