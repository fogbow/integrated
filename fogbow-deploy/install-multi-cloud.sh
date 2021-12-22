#!/bin/bash
# This script goal is to generate the Ansible hosts and ansible.cfg files

# Set path variables

SITE_CONF_FILE_NAME="./multi-cloud/conf-files/host.conf"
ANSIBLE_FILES_DIR_PATH="./multi-cloud/ansible-playbook"
ANSIBLE_HOSTS_FILE_PATH=$ANSIBLE_FILES_DIR_PATH/"hosts"
ANSIBLE_CFG_FILE_PATH=$ANSIBLE_FILES_DIR_PATH/"ansible.cfg"

function compress_component {
	tar -czf ../components_archives/$1.tar.gz ../$1
}

mkdir ../components_archives

compress_component accounting-service
compress_component authentication-service 
compress_component common
compress_component federated-network-service
compress_component finance-service
compress_component fogbow-gui
compress_component membership-service
compress_component resource-allocation-service
compress_component resource-catalog-gui
compress_component resource-catalog-service

# Generate content of Ansible hosts file

SERVICE_HOST_IP_PATTERN="service_host_ip"
SERVICE_HOST_IP=$(grep $SERVICE_HOST_IP_PATTERN $SITE_CONF_FILE_NAME | cut -d"=" -f2-)

SERVICE_HOST_PRIVATE_KEY_FILE_PATH_PATTERN="service_host_ssh_private_key_file"
SERVICE_HOST_PRIVATE_KEY_FILE_PATH=$(grep $SERVICE_HOST_PRIVATE_KEY_FILE_PATH_PATTERN $SITE_CONF_FILE_NAME | cut -d"=" -f2-)

echo "[localhost]" > $ANSIBLE_HOSTS_FILE_PATH
echo "127.0.0.1" >> $ANSIBLE_HOSTS_FILE_PATH
echo "" >> $ANSIBLE_HOSTS_FILE_PATH
echo "[service_host]" >> $ANSIBLE_HOSTS_FILE_PATH
echo $SERVICE_HOST_IP >> $ANSIBLE_HOSTS_FILE_PATH
echo "[service_host:vars]" >> $ANSIBLE_HOSTS_FILE_PATH
echo "ansible_ssh_private_key_file=$SERVICE_HOST_PRIVATE_KEY_FILE_PATH" >> $ANSIBLE_HOSTS_FILE_PATH
echo "ansible_python_interpreter=/usr/bin/python3" >> $ANSIBLE_HOSTS_FILE_PATH

# Generate content of Ansible ansible.cfg file

REMOTE_USER_PATTERN="^remote_user"
REMOTE_USER=$(grep $REMOTE_USER_PATTERN $SITE_CONF_FILE_NAME | cut -d"=" -f2-)

echo "[defaults]" > $ANSIBLE_CFG_FILE_PATH
echo "inventory = hosts" >> $ANSIBLE_CFG_FILE_PATH
echo "remote_user = $REMOTE_USER" >> $ANSIBLE_CFG_FILE_PATH
echo "host_key_checking = False" >> $ANSIBLE_CFG_FILE_PATH

# Deploy

(cd $ANSIBLE_FILES_DIR_PATH && ansible-playbook deploy.yml)
