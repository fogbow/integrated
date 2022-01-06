#!/bin/bash

function extract_components {
	for component in $*; do
		tar -xzf "components_archives/$component.tar.gz" -C "components_archives" >> log_extract
	done
}

function build_components_jars {
	for component in $*; do
		cd "components_archives/$component" && mvn install -Dmaven.test.skip=true >> log_jar && cd ../.. 
	done
}

function build_components_images {
	for component in $*; do
		cd "components_archives/$component" && ./build >> log_build && cd ../..
	done
}

extract_components accounting-service authentication-service common federated-network-service finance-service fogbow-gui membership-service resource-allocation-service

build_components_jars common authentication-service membership-service resource-allocation-service accounting-service
cd "components_archives/accounting-service" && mvn install -Dmaven.test.skip=true -f pom-jar.xml >> log_accs_jar && cd ../..

build_components_images common authentication-service resource-allocation-service membership-service accounting-service finance-service fogbow-gui
