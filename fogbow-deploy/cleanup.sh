#!/bin/bash

CONTAINER_IDS="`sudo docker ps --all | awk '{print $1}' | grep -v CONTAINER`"
sudo docker stop $CONTAINER_IDS
sudo docker rm $CONTAINER_IDS

IMAGES_IDS="`sudo docker images | awk '{print $3}' | grep -v IMAGE`"
sudo docker rmi $IMAGES_IDS
