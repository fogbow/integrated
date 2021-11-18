#!/bin/bash

CONF_FILE_PATH=$(pwd)/src/main/resources/private/rcs.conf
TARGET_CONF_FILE_PATH=$(pwd)/target/classes/private/rcs.conf
PROPERTIE_KEY=$1
PROPERTIE_VALUE=$2

sed -i "s/$PROPERTIE_KEY.*/$PROPERTIE_KEY=$PROPERTIE_VALUE/" $CONF_FILE_PATH
sed -i "s/$PROPERTIE_KEY.*/$PROPERTIE_KEY=$PROPERTIE_VALUE/" $TARGET_CONF_FILE_PATH

exit $?