#!/bin/bash

set -ex

DOCKER_CACHE_DIR=$HOME/docker-images-cached

mkdir -p ${DOCKER_CACHE_DIR}

CENTOS_7_IMAGE_NAME="fsat/centos-7-jdk-8-sbt:latest"
CENTOS_7_IMAGE_FILE="${DOCKER_CACHE_DIR}/fsat_centos-7-jdk-8-sbt_latest.tar"

if [ ! -e ${CENTOS_7_IMAGE_FILE} ]
then
    echo "Pulling ${CENTOS_7_IMAGE_NAME}"
    docker pull ${CENTOS_7_IMAGE_NAME}

    echo "Saving ${CENTOS_7_IMAGE_NAME} into ${CENTOS_7_IMAGE_FILE}"
    docker save -o ${CENTOS_7_IMAGE_FILE} ${CENTOS_7_IMAGE_NAME}
fi

echo "Loading ${CENTOS_7_IMAGE_NAME} from ${CENTOS_7_IMAGE_FILE}"
docker load -i ${CENTOS_7_IMAGE_FILE}

XENIAL_IMAGE_NAME="fsat/xenial-jdk-8-sbt:latest"
XENIAL_IMAGE_FILE="${DOCKER_CACHE_DIR}/fsat_xenial-jdk-8-sbt_latest.tar"

if [ ! -e ${XENIAL_IMAGE_FILE} ]
then
    echo "Pulling ${XENIAL_IMAGE_NAME}"
    docker pull ${XENIAL_IMAGE_NAME}

    echo "Saving ${XENIAL_IMAGE_NAME} into ${XENIAL_IMAGE_FILE}"
    docker save -o ${XENIAL_IMAGE_FILE} ${XENIAL_IMAGE_NAME}
fi

echo "Loading ${XENIAL_IMAGE_NAME} from ${XENIAL_IMAGE_FILE}"
docker load -i ${XENIAL_IMAGE_FILE}
