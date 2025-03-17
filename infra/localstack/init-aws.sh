#!/bin/bash

set -euo pipefail
set -x

export AWS_ACCESS_KEY_ID=000000000000 AWS_SECRET_ACCESS_KEY=000000000000

create_bucket() {
  local BUCKET_NAME_TO_CREATE=$1
  awslocal s3api create-bucket --bucket "${BUCKET_NAME_TO_CREATE}" --region us-east-1
}

echo "setting up s3"
create_bucket "local-images"
echo "s3 created succesfully"
