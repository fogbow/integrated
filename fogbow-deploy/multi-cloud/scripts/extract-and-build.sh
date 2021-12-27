#!/bin/bash

function extract_component {
        tar -xzf "components_archives/$1.tar.gz" -C "components_archives" >> log_extract
}

function build_component_image {
	cd "components_archives/$1" && ./build >> log_build && cd ../..
}

function build_component_jar {
	cd "components_archives/$1" && mvn install -Dmaven.test.skip=true >> log_jar && cd ../.. 
}

extract_component accounting-service
extract_component authentication-service
extract_component common
extract_component federated-network-service
extract_component finance-service
extract_component fogbow-gui
extract_component membership-service
extract_component resource-allocation-service
extract_component resource-catalog-gui
extract_component resource-catalog-service

build_component_jar common
build_component_jar authentication-service
build_component_jar membership-service
build_component_jar resource-allocation-service
build_component_jar accounting-service
#build_component_jar resource-allocation-service

build_component_image common
build_component_image authentication-service
build_component_image resource-allocation-service
build_component_image membership-service
build_component_image accounting-service
build_component_image fogbow-gui