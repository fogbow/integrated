#!/bin/bash

# Source configuration files
CONF_FILES_DIR_PATH="../conf-files"
AS_CONF_FILE_PATH=$CONF_FILES_DIR_PATH/"as.conf"
FS_CONF_FILE_PATH=$CONF_FILES_DIR_PATH/"fs.conf"
SERVICE_CONF_FILE_PATH=$CONF_FILES_DIR_PATH/"service.conf"
HOST_CONF_FILE_PATH=$CONF_FILES_DIR_PATH/"host.conf"
TEMPLATES_DIR_PATH="../templates"
COMMON_TEMPLATES_DIR_PATH="../../common/templates"
COMMON_SCRIPTS_DIR_PATH="../../common/scripts"

# Reading configuration files

## Reading data from service.conf
### Service ports configuration
AS_PORT_PATTERN="As_port"
AS_PORT=$(grep $AS_PORT_PATTERN $SERVICE_CONF_FILE_PATH | cut -d"=" -f2-)
RAS_PORT_PATTERN="Ras_port"
RAS_PORT=$(grep $RAS_PORT_PATTERN $SERVICE_CONF_FILE_PATH | cut -d"=" -f2-)
ACCS_PORT_PATTERN="Accs_port"
ACCS_PORT=$(grep $ACCS_PORT_PATTERN $SERVICE_CONF_FILE_PATH | cut -d"=" -f2-)
FS_PORT_PATTERN="Fs_port"
FS_PORT=$(grep $FS_PORT_PATTERN $SERVICE_CONF_FILE_PATH | cut -d"=" -f2-)
GUI_PORT_PATTERN="Gui_port"
GUI_PORT=$(grep $GUI_PORT_PATTERN $SERVICE_CONF_FILE_PATH | cut -d"=" -f2-)
DB_PORT_PATTERN="Db_port"
DB_PORT=$(grep $DB_PORT_PATTERN $SERVICE_CONF_FILE_PATH | cut -d"=" -f2-)
HTTP_PORT_PATTERN="Http_port"
HTTP_PORT=$(grep $HTTP_PORT_PATTERN $SERVICE_CONF_FILE_PATH | cut -d"=" -f2-)
HTTPS_PORT_PATTERN="Https_port"
HTTPS_PORT=$(grep $HTTPS_PORT_PATTERN $SERVICE_CONF_FILE_PATH | cut -d"=" -f2-)
### Service tags configuration
AS_TAG_PATTERN="As_tag"
AS_TAG=$(grep $AS_TAG_PATTERN $SERVICE_CONF_FILE_PATH | cut -d"=" -f2-)
if [ -z ${AS_TAG// } ]; then
	AS_TAG="latest"
fi
RAS_TAG_PATTERN="Ras_tag"
RAS_TAG=$(grep $RAS_TAG_PATTERN $SERVICE_CONF_FILE_PATH | cut -d"=" -f2-)
if [ -z ${RAS_TAG// } ]; then
	RAS_TAG="latest"
fi
ACCS_TAG_PATTERN="Accs_tag"
ACCS_TAG=$(grep $ACCS_TAG_PATTERN $SERVICE_CONF_FILE_PATH | cut -d"=" -f2-)
if [ -z ${ACCS_TAG// } ]; then
	ACCS_TAG="latest"
fi
GUI_TAG_PATTERN="Gui_tag"
GUI_TAG=$(grep $GUI_TAG_PATTERN $SERVICE_CONF_FILE_PATH | cut -d"=" -f2-)
if [ -z ${GUI_TAG// } ]; then
	GUI_TAG="latest"
fi
DB_TAG_PATTERN="Db_tag"
DB_TAG=$(grep $DB_TAG_PATTERN $SERVICE_CONF_FILE_PATH | cut -d"=" -f2-)
if [ -z ${DB_TAG// } ]; then
	DB_TAG="latest"
fi
APACHE_TAG_PATTERN="Apache_tag"
APACHE_TAG=$(grep $APACHE_TAG_PATTERN $SERVICE_CONF_FILE_PATH | cut -d"=" -f2-)
if [ -z ${APACHE_TAG// } ]; then
	APACHE_TAG="latest"
fi

## Reading data from host.conf
SERVICE_HOST_IP_PATTERN="service_host_ip"
SERVICE_HOST_IP=$(grep $SERVICE_HOST_IP_PATTERN $HOST_CONF_FILE_PATH | cut -d"=" -f2-)
PROVIDER_ID_PATTERN="service_host_FQDN"
PROVIDER_ID=$(grep $PROVIDER_ID_PATTERN $HOST_CONF_FILE_PATH | cut -d"=" -f2-)
PROVIDER_ID_TAG="provider_id"

## Reading data from as.conf
AT_PATTERN="authentication_type"
AT=$(grep $AT_PATTERN $AS_CONF_FILE_PATH | cut -d"=" -f2-)
OS_PATTERN="openstack_keystone_v3_url"
OS=$(grep $OS_PATTERN $AS_CONF_FILE_PATH | cut -d"=" -f2-)
CS_PATTERN="cloudstack_url"
CS=$(grep $CS_PATTERN $AS_CONF_FILE_PATH | cut -d"=" -f2-)
ONE_PATTERN="opennebula_url"
ONE=$(grep $ONE_PATTERN $AS_CONF_FILE_PATH | cut -d"=" -f2-)
LEP_PATTERN="ldap_url"
LEP=$(grep $LEP_PATTERN $AS_CONF_FILE_PATH | cut -d"=" -f2-)
LB_PATTERN="ldap_base"
LB=$(grep $LB_PATTERN $AS_CONF_FILE_PATH | cut -d"=" -f2-)
LET_PATTERN="ldap_encrypt_type"
LET=$(grep $LET_PATTERN $AS_CONF_FILE_PATH | cut -d"=" -f2-)
DSP_PATTERN="domain_service_provider"
DSP=$(grep $DSP_PATTERN $AS_CONF_FILE_PATH | cut -d"=" -f2-)
DSU_PATTERN="discovery_service_url"
DSU=$(grep $DSU_PATTERN $AS_CONF_FILE_PATH | cut -d"=" -f2-)
DSMU_PATTERN="discovery_service_metadata_url"
DSMU=$(grep $DSMU_PATTERN $AS_CONF_FILE_PATH | cut -d"=" -f2-)

## Reading data from fs.conf
FS_MANAGER_USERNAME_PATTERN="manager_username"
FS_MANAGER_USERNAME=$(grep $FS_MANAGER_USERNAME_PATTERN $FS_CONF_FILE_PATH | cut -d"=" -f2-)
FS_MANAGER_PASSWORD_PATTERN="manager_password"
FS_MANAGER_PASSWORD=$(grep $FS_MANAGER_PASSWORD_PATTERN $FS_CONF_FILE_PATH | cut -d"=" -f2-)
FS_ADMIN_IDS_PATTERN="admin_ids"
FS_ADMIN_IDS=$(grep $FS_ADMIN_IDS_PATTERN $FS_CONF_FILE_PATH | cut -d"=" -f2-)

FS_DEFAULT_PLAN_PLUGIN_TYPE_PATTERN="default_plan_plugin_type"
FS_DEFAULT_PLAN_PLUGIN_TYPE=$(grep $FS_DEFAULT_PLAN_PLUGIN_TYPE_PATTERN $FS_CONF_FILE_PATH | cut -d"=" -f2-)
FS_PLAN_PLUGIN_NAME_PATTERN="plan_plugin_name"
FS_PLAN_PLUGIN_NAME=$(grep $FS_PLAN_PLUGIN_NAME_PATTERN $FS_CONF_FILE_PATH | cut -d"=" -f2-)
FS_TIME_TO_WAIT_BEFORE_STOPPING_PATTERN="time_to_wait_before_stopping"
FS_TIME_TO_WAIT_BEFORE_STOPPING=$(grep $FS_TIME_TO_WAIT_BEFORE_STOPPING_PATTERN $FS_CONF_FILE_PATH | cut -d"=" -f2-)
FS_DEFAULT_RESOURCE_VALUE_PATTERN="finance_plan_default_resource_value"
FS_DEFAULT_RESOURCE_VALUE=$(grep $FS_DEFAULT_RESOURCE_VALUE_PATTERN $FS_CONF_FILE_PATH | cut -d"=" -f2-)

if [ "$FS_DEFAULT_PLAN_PLUGIN_TYPE" == "cloud.fogbow.fs.core.plugins.plan.postpaid.PostPaidPlanPlugin" ]; then
	FS_BILLING_INTERVAL_PATTERN="billing_interval"
	FS_BILLING_INTERVAL=$(grep $FS_BILLING_INTERVAL_PATTERN $FS_CONF_FILE_PATH | cut -d"=" -f2-)
	FS_INVOICE_WAIT_TIME_PATTERN="invoice_wait_time"
	FS_INVOICE_WAIT_TIME=$(grep $FS_INVOICE_WAIT_TIME_PATTERN $FS_CONF_FILE_PATH | cut -d"=" -f2-)
elif [ "$FS_DEFAULT_PLAN_PLUGIN_TYPE" == "cloud.fogbow.fs.core.plugins.plan.prepaid.PrePaidPlanPlugin" ]; then
	FS_CREDITS_DEDUCTION_WAIT_TIME_PATTERN="credits_deduction_wait_time"
	FS_CREDITS_DEDUCTION_WAIT_TIME=$(grep $FS_CREDITS_DEDUCTION_WAIT_TIME_PATTERN $FS_CONF_FILE_PATH | cut -d"=" -f2-)
else
    echo "Fatal error: invalid default finance plan type: [$FS_DEFAULT_PLAN_PLUGIN_TYPE]"
    exit 1
fi


# Creating temporary directory
mkdir -p ./tmp/conf-files

# Generating secrets
DB_PASSWORD_PROPERTY="db_password"
DB_PASSWORD=$(pwgen 10 1)

# Generating deploy-and-start-services.sh
DEPLOY_START_SERVICES_FILE_NAME="deploy-and-start-services.sh"
cp $DEPLOY_START_SERVICES_FILE_NAME ./tmp/$DEPLOY_START_SERVICES_FILE_NAME
chmod 600 ./tmp/$DEPLOY_START_SERVICES_FILE_NAME
sed -i "s|$DB_PASSWORD_PROPERTY|$DB_PASSWORD|g" ./tmp/$DEPLOY_START_SERVICES_FILE_NAME

# Generating extract-and-build.sh
EXTRACT_BUILD_FILE_NAME="extract-and-build.sh"
cp $EXTRACT_BUILD_FILE_NAME ./tmp/$EXTRACT_BUILD_FILE_NAME
chmod 600 ./tmp/$EXTRACT_BUILD_FILE_NAME

# Ports and tags conf-file generation
PORTS_TAGS_CONF_FILE_PATH="./tmp/conf-files/service.conf"
touch $PORTS_TAGS_CONF_FILE_PATH
echo "$AS_PORT_PATTERN=$AS_PORT" > $PORTS_TAGS_CONF_FILE_PATH
echo "$RAS_PORT_PATTERN=$RAS_PORT" >> $PORTS_TAGS_CONF_FILE_PATH
echo "$ACCS_PORT_PATTERN=$ACCS_PORT" >> $PORTS_TAGS_CONF_FILE_PATH
echo "$FS_PORT_PATTERN=$FS_PORT" >> $PORTS_TAGS_CONF_FILE_PATH
echo "$GUI_PORT_PATTERN=$GUI_PORT" >> $PORTS_TAGS_CONF_FILE_PATH
echo "$DB_PORT_PATTERN=$DB_PORT" >> $PORTS_TAGS_CONF_FILE_PATH
echo "$HTTP_PORT_PATTERN=$HTTP_PORT" >> $PORTS_TAGS_CONF_FILE_PATH
echo "$HTTPS_PORT_PATTERN=$HTTPS_PORT" >> $PORTS_TAGS_CONF_FILE_PATH
echo "$AS_TAG_PATTERN=$AS_TAG" >> $PORTS_TAGS_CONF_FILE_PATH
echo "$RAS_TAG_PATTERN=$RAS_TAG" >> $PORTS_TAGS_CONF_FILE_PATH
echo "$ACCS_TAG_PATTERN=$ACCS_TAG" >> $PORTS_TAGS_CONF_FILE_PATH
echo "$GUI_TAG_PATTERN=$GUI_TAG" >> $PORTS_TAGS_CONF_FILE_PATH
echo "$DB_TAG_PATTERN=$DB_TAG" >> $PORTS_TAGS_CONF_FILE_PATH
echo "$APACHE_TAG_PATTERN=$APACHE_TAG" >> $PORTS_TAGS_CONF_FILE_PATH

# AS conf-file generation
## Setting AS variables
AS_DIR_PATH="./tmp/conf-files/as"
AS_CONF_FILE_NAME="as.conf"
AS_CONTAINER_CONF_FILE_DIR_PATH="/root/authentication-service/src/main/resources/private"
AS_PRIVATE_KEY_PATH=$AS_DIR_PATH/"id_rsa"
AS_PUBLIC_KEY_PATH=$AS_DIR_PATH/"id_rsa.pub"
AS_RSA_KEY_PATH=$AS_DIR_PATH/"rsa_key.pem"
## Creating directory
mkdir -p $AS_DIR_PATH
## Adding properties
echo "# Authentication plugin specific properties" > $AS_DIR_PATH/$AS_CONF_FILE_NAME
SIP_PATTERN="system_identity_provider_plugin_class"
case $AT in
  "ldap")
    SIP="cloud.fogbow.as.core.systemidp.plugins.ldap.LdapSystemIdentityProviderPlugin"
    ;;
  "shibboleth")
    SIP="cloud.fogbow.as.core.systemidp.plugins.shibboleth.ShibbolethSystemIdentityProviderPlugin"
    ;;
  "openstack")
    SIP="cloud.fogbow.as.core.systemidp.plugins.openstack.v3.OpenStackSystemIdentityProviderPlugin"
    ;;
  "cloudstack")
    SIP="cloud.fogbow.as.core.systemidp.plugins.cloudstack.CloudStackSystemIdentityProviderPlugin"
    ;;
  "opennebula")
    SIP="cloud.fogbow.as.core.systemidp.plugins.opennebula.OpenNebulaSystemIdentityProviderPlugin"
    ;;
  "googlecloud")
    SIP="cloud.fogbow.as.core.systemidp.plugins.googlecloud.GoogleCloudSystemIdentityProviderPlugin"
    ;;
  *)
    echo "Fatal error: invalid authentication type: [$AT]"
    exit 1
    ;;
esac
echo $SIP_PATTERN=$SIP >> $AS_DIR_PATH/$AS_CONF_FILE_NAME
echo $OS_PATTERN=$OS >> $AS_DIR_PATH/$AS_CONF_FILE_NAME
echo $CS_PATTERN=$CS >> $AS_DIR_PATH/$AS_CONF_FILE_NAME
echo $ONE_PATTERN=$ONE >> $AS_DIR_PATH/$AS_CONF_FILE_NAME
echo $LEP_PATTERN=$LEP >> $AS_DIR_PATH/$AS_CONF_FILE_NAME
echo $LB_PATTERN=$LB >> $AS_DIR_PATH/$AS_CONF_FILE_NAME
echo $LET_PATTERN=$LET >> $AS_DIR_PATH/$AS_CONF_FILE_NAME
echo "" >> $AS_DIR_PATH/$AS_CONF_FILE_NAME
echo $PROVIDER_ID_TAG=$PROVIDER_ID >> $AS_DIR_PATH/$AS_CONF_FILE_NAME
## Creating and adding key pair
echo "" >> $AS_DIR_PATH/$AS_CONF_FILE_NAME
openssl genrsa -out $AS_RSA_KEY_PATH 1024
openssl pkcs8 -topk8 -in $AS_RSA_KEY_PATH -out $AS_PRIVATE_KEY_PATH -nocrypt
openssl rsa -in $AS_PRIVATE_KEY_PATH -outform PEM -pubout -out $AS_PUBLIC_KEY_PATH
chmod 600 $AS_PRIVATE_KEY_PATH
rm $AS_RSA_KEY_PATH
echo "public_key_file_path="$AS_CONTAINER_CONF_FILE_DIR_PATH/"id_rsa.pub" >> $AS_DIR_PATH/$AS_CONF_FILE_NAME
echo "private_key_file_path="$AS_CONTAINER_CONF_FILE_DIR_PATH/"id_rsa" >> $AS_DIR_PATH/$AS_CONF_FILE_NAME

# RAS conf-file generation
## Setting RAS variables
RAS_DIR_PATH="./tmp/conf-files/ras"
RAS_CONF_FILE_NAME="ras.conf"
CLOUDS_DIR_NAME="clouds"
APPLICATION_PROPERTIES_FILE_NAME="application.properties"
RAS_CONTAINER_CONF_FILE_DIR_PATH="/root/resource-allocation-service/src/main/resources/private"
RAS_PRIVATE_KEY_PATH=$RAS_DIR_PATH/"id_rsa"
RAS_PUBLIC_KEY_PATH=$RAS_DIR_PATH/"id_rsa.pub"
RAS_RSA_KEY_PATH=$RAS_DIR_PATH/"rsa_key.pem"
## Creating directory
mkdir -p $RAS_DIR_PATH
touch $RAS_DIR_PATH/$RAS_CONF_FILE_NAME
chmod 600 $RAS_DIR_PATH/$RAS_CONF_FILE_NAME
## Adding properties
CN_PATTERN="cloud_names"
CN=""
for i in `ls $CONF_FILES_DIR_PATH/$CLOUDS_DIR_NAME`
do
  if [ -f "$CONF_FILES_DIR_PATH/$CLOUDS_DIR_NAME/$i" ]; then
    CN=$CN`basename $i .conf`","
  fi
done
CN=`echo $CN | sed 's/.$//'`
echo "# Comma-separated list of the names of the clouds managed by this RAS" > $RAS_DIR_PATH/$RAS_CONF_FILE_NAME
echo "# Their configuration is stored under the directory clouds/<name>" >> $RAS_DIR_PATH/$RAS_CONF_FILE_NAME
echo "# The default cloud is the first name in the list" >> $RAS_DIR_PATH/$RAS_CONF_FILE_NAME
echo $CN_PATTERN=$CN >> $RAS_DIR_PATH/$RAS_CONF_FILE_NAME
echo "authorization_plugin_class=cloud.fogbow.ras.core.plugins.authorization.DefaultAuthorizationPlugin" >> $RAS_DIR_PATH/$RAS_CONF_FILE_NAME
echo "" >> $RAS_DIR_PATH/$RAS_CONF_FILE_NAME
echo $PROVIDER_ID_TAG=$PROVIDER_ID >> $RAS_DIR_PATH/$RAS_CONF_FILE_NAME
echo "xmpp_enabled=false" >> $RAS_DIR_PATH/$RAS_CONF_FILE_NAME
PROTOCOL="http://"
echo "as_url=$PROTOCOL$SERVICE_HOST_IP" >> $RAS_DIR_PATH/$RAS_CONF_FILE_NAME
echo "as_port=$AS_PORT" >> $RAS_DIR_PATH/$RAS_CONF_FILE_NAME
echo "" >> $RAS_DIR_PATH/$RAS_CONF_FILE_NAME
echo "jdbc_database_url=jdbc:sqlite:/root/resource-allocation-service/ras.db" >> $RAS_DIR_PATH/$RAS_CONF_FILE_NAME
## Creating and adding key pair
echo "" >> $RAS_DIR_PATH/$RAS_CONF_FILE_NAME
openssl genrsa -out $RAS_RSA_KEY_PATH 2048
openssl pkcs8 -topk8 -in $RAS_RSA_KEY_PATH -out $RAS_PRIVATE_KEY_PATH -nocrypt
openssl rsa -in $RAS_PRIVATE_KEY_PATH -outform PEM -pubout -out $RAS_PUBLIC_KEY_PATH
chmod 600 $RAS_PRIVATE_KEY_PATH
rm $RAS_RSA_KEY_PATH
echo "public_key_file_path="$RAS_CONTAINER_CONF_FILE_DIR_PATH/"id_rsa.pub" >> $RAS_DIR_PATH/$RAS_CONF_FILE_NAME
echo "private_key_file_path="$RAS_CONTAINER_CONF_FILE_DIR_PATH/"id_rsa" >> $RAS_DIR_PATH/$RAS_CONF_FILE_NAME
## Generating cloud configuration files
for i in `ls $CONF_FILES_DIR_PATH/$CLOUDS_DIR_NAME`
do
  if [ -f "$CONF_FILES_DIR_PATH/$CLOUDS_DIR_NAME/$i" ]; then
    bash $COMMON_SCRIPTS_DIR_PATH/generate_cloud_conf.sh $CONF_FILES_DIR_PATH/$CLOUDS_DIR_NAME/$i $RAS_DIR_PATH $COMMON_TEMPLATES_DIR_PATH
    retVal=$?
    if [ $retVal -ne 0 ]; then
        exit $retVal
    fi
  fi
done
## Copying application.properties file
yes | cp -f $COMMON_TEMPLATES_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME".ras" $RAS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME
chmod 600 $RAS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME
## Editing application.properties
JDBC_PREFIX="jdbc:postgresql:"
RAS_DB_ENDPOINT="ras"
DB_URL_PROPERTY="spring.datasource.url"
DB_URL=$JDBC_PREFIX"//"$SERVICE_HOST_IP":"$DB_PORT"/"$RAS_DB_ENDPOINT
echo "" >> $RAS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME
echo "$DB_URL_PROPERTY=$DB_URL" >> $RAS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME
DB_USERNAME="fogbow"
DB_USERNAME_PATTERN="spring.datasource.username"
echo "$DB_USERNAME_PATTERN=$DB_USERNAME" >> $RAS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME
DB_PASSWORD_PATTERN="spring.datasource.password"
echo "$DB_PASSWORD_PATTERN=$DB_PASSWORD" >> $RAS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME

# MS conf-file generation
MS_DIR_PATH="./tmp/conf-files/ms"
MS_CONF_FILE_NAME="ms.conf"
MS_CONTAINER_CONF_FILE_DIR_PATH="/root/membership-service/src/main/resources/private"
MS_PRIVATE_KEY_PATH=$MS_DIR_PATH/"id_rsa"
MS_PUBLIC_KEY_PATH=$MS_DIR_PATH/"id_rsa.pub"
MS_RSA_KEY_PATH=$MS_DIR_PATH/"rsa_key.pem"

## Creating directory
mkdir -p $MS_DIR_PATH


# ACCS conf-file generation
ACCS_DIR_PATH="./tmp/conf-files/accs"
ACCS_CONF_FILE_NAME="accs.conf"
ACCS_CONTAINER_CONF_FILE_DIR_PATH="/root/accounting-service/src/main/resources/private"
ACCS_PRIVATE_KEY_PATH=$ACCS_DIR_PATH/"id_rsa"
ACCS_PUBLIC_KEY_PATH=$ACCS_DIR_PATH/"id_rsa.pub"
ACCS_RSA_KEY_PATH=$ACCS_DIR_PATH/"rsa_key.pem"

## Creating directory
mkdir -p $ACCS_DIR_PATH
touch $ACCS_DIR_PATH/$ACCS_CONF_FILE_NAME
chmod 600 $ACCS_DIR_PATH/$ACCS_CONF_FILE_NAME

# TODO need to add keys for ACCS
## Copying application.properties file
yes | cp -f $COMMON_TEMPLATES_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME".accs" $ACCS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME
chmod 600 $ACCS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME
## Editing application.properties
JDBC_PREFIX="jdbc:postgresql:"
ACCS_DB_ENDPOINT="accs"
ACCS_DB_URL_PROPERTY="spring.accounting-datasource.jdbcUrl"
ACCS_DB_DRIVER_CLASS_NAME_PROPERTY="spring.accounting-datasource.driverClassName"
ACCS_DB_USERNAME_PROPERTY="spring.accounting-datasource.username"
ACCS_DB_PASSWORD_PROPERTY="spring.accounting-datasource.password"
ACCS_DB_URL=$JDBC_PREFIX"//"$SERVICE_HOST_IP":"$DB_PORT"/"$ACCS_DB_ENDPOINT
ACCS_DB_DRIVER_CLASS_NAME="org.postgresql.Driver"
ACCS_DB_USERNAME="fogbow"

RAS_DB_ENDPOINT="ras"
RAS_DB_URL_PROPERTY="spring.order-datasource.jdbcUrl"
RAS_DB_DRIVER_CLASS_NAME_PROPERTY="spring.order-datasource.driverClassName"
RAS_DB_USERNAME_PROPERTY="spring.order-datasource.username"
RAS_DB_PASSWORD_PROPERTY="spring.order-datasource.password"
RAS_DB_URL=$JDBC_PREFIX"//"$SERVICE_HOST_IP":"$DB_PORT"/"$RAS_DB_ENDPOINT
RAS_DB_DRIVER_CLASS_NAME="org.postgresql.Driver"
RAS_DB_USERNAME="fogbow"

echo "" >> $ACCS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME
echo "$ACCS_DB_URL_PROPERTY=$ACCS_DB_URL" >> $ACCS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME
echo "$ACCS_DB_DRIVER_CLASS_NAME_PROPERTY=$ACCS_DB_DRIVER_CLASS_NAME" >> $ACCS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME
echo "$ACCS_DB_USERNAME_PROPERTY=$ACCS_DB_USERNAME" >> $ACCS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME
echo "$ACCS_DB_PASSWORD_PROPERTY=$DB_PASSWORD" >> $ACCS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME
echo "$RAS_DB_URL_PROPERTY=$RAS_DB_URL" >> $ACCS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME
echo "$RAS_DB_DRIVER_CLASS_NAME_PROPERTY=$RAS_DB_DRIVER_CLASS_NAME" >> $ACCS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME
echo "$RAS_DB_USERNAME_PROPERTY=$RAS_DB_USERNAME" >> $ACCS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME
echo "$RAS_DB_PASSWORD_PROPERTY=$DB_PASSWORD" >> $ACCS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME

# FS conf-file generation
FS_DIR_PATH="./tmp/conf-files/fs"
FS_CONF_FILE_NAME="fs.conf"
FS_CONTAINER_CONF_FILE_DIR_PATH="/root/finance-service/src/main/resources/private"
FS_PRIVATE_KEY_PATH=$FS_DIR_PATH/"id_rsa"
FS_PUBLIC_KEY_PATH=$FS_DIR_PATH/"id_rsa.pub"
FS_RSA_KEY_PATH=$FS_DIR_PATH/"rsa_key.pem"
FS_DEFAULT_FINANCE_PLAN_FILE_NAME="default_fs_finance_plan"
FS_DEFAULT_FINANCE_PLAN_FILE_PATH="../conf-files/$FS_DEFAULT_FINANCE_PLAN_FILE_NAME"

## Creating directory
mkdir -p $FS_DIR_PATH
touch $FS_DIR_PATH/$FS_CONF_FILE_NAME
chmod 600 $FS_DIR_PATH/$FS_CONF_FILE_NAME
cp $FS_DEFAULT_FINANCE_PLAN_FILE_PATH $FS_DIR_PATH

## Creating and adding key pair
echo "" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
openssl genrsa -out $FS_RSA_KEY_PATH 2048
openssl pkcs8 -topk8 -in $FS_RSA_KEY_PATH -out $FS_PRIVATE_KEY_PATH -nocrypt
openssl rsa -in $FS_PRIVATE_KEY_PATH -outform PEM -pubout -out $FS_PUBLIC_KEY_PATH
chmod 600 $FS_PRIVATE_KEY_PATH
rm $FS_RSA_KEY_PATH

## Adding properties
echo "public_key_file_path="$FS_CONTAINER_CONF_FILE_DIR_PATH/"id_rsa.pub" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
echo "private_key_file_path="$FS_CONTAINER_CONF_FILE_DIR_PATH/"id_rsa" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
echo "" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
# FIXME for now we use AdminAuthorizationPlugin, but this plugin is limited and
# should be replaced by the RoleAuthorizationPlugin later.
FS_AUTHORIZATION_PLUGIN="cloud.fogbow.fs.core.plugins.authorization.AdminAuthorizationPlugin"
echo "authorization_plugin_class=$FS_AUTHORIZATION_PLUGIN" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
echo "admin_ids=$FS_ADMIN_IDS" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
echo "" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
PROTOCOL="http://"
echo "as_url=$PROTOCOL$SERVICE_HOST_IP" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
echo "as_port=$AS_PORT" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
echo "manager_username=$FS_MANAGER_USERNAME" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
echo "manager_password=$FS_MANAGER_PASSWORD" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
echo "accs_url=$PROTOCOL$SERVICE_HOST_IP" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
echo "accs_port=$ACCS_PORT" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
echo "ras_url=$PROTOCOL$SERVICE_HOST_IP" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
echo "ras_port=$RAS_PORT" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
echo "" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
echo "default_plan_plugin_type=$FS_DEFAULT_PLAN_PLUGIN_TYPE" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
echo "plan_plugin_name=$FS_PLAN_PLUGIN_NAME" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME

echo "finance_plan_file_path=src/main/resources/private/default_fs_finance_plan" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
echo "time_to_wait_before_stopping=$FS_TIME_TO_WAIT_BEFORE_STOPPING" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
echo "finance_plan_default_resource_value=$FS_DEFAULT_RESOURCE_VALUE" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME

if [ "$FS_DEFAULT_PLAN_PLUGIN_TYPE" == "cloud.fogbow.fs.core.plugins.plan.postpaid.PostPaidPlanPlugin" ]; then
	echo "billing_interval=$FS_BILLING_INTERVAL" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
	echo "invoice_wait_time=$FS_INVOICE_WAIT_TIME" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
else
	echo "credits_deduction_wait_time$FS_CREDITS_DEDUCTION_WAIT_TIME" >> $FS_DIR_PATH/$FS_CONF_FILE_NAME
fi

## Copying application.properties file
yes | cp -f $COMMON_TEMPLATES_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME".fs" $FS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME
chmod 600 $FS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME

## Editing application.properties
JDBC_PREFIX="jdbc:postgresql:"
FS_DB_ENDPOINT="fs"
DB_URL_PROPERTY="spring.datasource.url"
DB_URL=$JDBC_PREFIX"//"$SERVICE_HOST_IP":"$DB_PORT"/"$FS_DB_ENDPOINT
echo "" >> $RAS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME
echo "$DB_URL_PROPERTY=$DB_URL" >> $FS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME
DB_USERNAME="fogbow"
DB_USERNAME_PATTERN="spring.datasource.username"
echo "$DB_USERNAME_PATTERN=$DB_USERNAME" >> $FS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME
DB_PASSWORD_PATTERN="spring.datasource.password"
echo "$DB_PASSWORD_PATTERN=$DB_PASSWORD" >> $FS_DIR_PATH/$APPLICATION_PROPERTIES_FILE_NAME

# GUI conf-file generation
## Setting AS variables
GUI_DIR_PATH="./tmp/conf-files/gui"
GUI_CONF_FILE_NAME="api.config.js"
## Creating directory
mkdir -p $GUI_DIR_PATH
## Copying configuration template
yes | cp -f $TEMPLATES_DIR_PATH/$AT"-"$GUI_CONF_FILE_NAME $GUI_DIR_PATH/$GUI_CONF_FILE_NAME
# Setting endpoints
sed -i "s#.*\<as\>:.*#	as: 'https://$PROVIDER_ID/as',#" $GUI_DIR_PATH/$GUI_CONF_FILE_NAME
sed -i "s#.*ras:.*#	ras: 'https://$PROVIDER_ID/ras',#" $GUI_DIR_PATH/$GUI_CONF_FILE_NAME
sed -i "s#.*local:.*#	local: '$PROVIDER_ID',#" $GUI_DIR_PATH/$GUI_CONF_FILE_NAME
## Setting deployType property
sed -i "s#.*deployType.*#	deployType: 'multi-cloud',#" $GUI_DIR_PATH/$GUI_CONF_FILE_NAME
sed -i "s#.*fnsServiceNames.*#	fnsServiceNames: [],#" $GUI_DIR_PATH/$GUI_CONF_FILE_NAME
if [ "$AT" == "shibboleth" ]; then
  sed -i "s#.*\<remoteCredentialsUrl\>:.*#remoteCredentialsUrl: 'https://$DSP',#" $GUI_DIR_PATH/$GUI_CONF_FILE_NAME
fi

# Apache conf-file generation
## Setting apache variables
APACHE_DIR_PATH="./tmp/conf-files/apache"
PORTS_FILE_NAME="ports.conf"
APACHE_VHOST_FILE_NAME="000-default.conf"
ROOT_WWW_FILE_NAME="index.html"
CERTIFICATE_FILE_PATH=$CONF_FILES_DIR_PATH/"certs/site.crt"
CERTIFICATE_KEY_FILE_PATH=$CONF_FILES_DIR_PATH/"certs/site.key"
CERTIFICATE_CHAIN_FILE_PATH=$CONF_FILES_DIR_PATH/"certs/site.pem"
## Creating directory
mkdir -p $APACHE_DIR_PATH
## Copying certificate files
yes | cp -f $CERTIFICATE_FILE_PATH $APACHE_DIR_PATH
yes | cp -f $CERTIFICATE_KEY_FILE_PATH $APACHE_DIR_PATH
yes | cp -f $CERTIFICATE_CHAIN_FILE_PATH $APACHE_DIR_PATH
## Copying ports.conf
yes | cp -f $COMMON_TEMPLATES_DIR_PATH/$PORTS_FILE_NAME $APACHE_DIR_PATH
## Generating Virtual Host file
yes | cp -f $TEMPLATES_DIR_PATH/$APACHE_VHOST_FILE_NAME $APACHE_DIR_PATH
sed -i "s|$SERVICE_HOST_IP_PATTERN|$SERVICE_HOST_IP|g" $APACHE_DIR_PATH/$APACHE_VHOST_FILE_NAME
sed -i "s|$PROVIDER_ID_PATTERN|$PROVIDER_ID|g" $APACHE_DIR_PATH/$APACHE_VHOST_FILE_NAME
sed -i "s|$AS_PORT_PATTERN|$AS_PORT|g" $APACHE_DIR_PATH/$APACHE_VHOST_FILE_NAME
sed -i "s|$RAS_PORT_PATTERN|$RAS_PORT|g" $APACHE_DIR_PATH/$APACHE_VHOST_FILE_NAME
sed -i "s|$GUI_PORT_PATTERN|$GUI_PORT|g" $APACHE_DIR_PATH/$APACHE_VHOST_FILE_NAME
## Generating index.html
yes | cp -f $TEMPLATES_DIR_PATH/$ROOT_WWW_FILE_NAME $APACHE_DIR_PATH
sed -i "s|$SERVICE_HOST_IP_PATTERN|$SERVICE_HOST_IP|g" $APACHE_DIR_PATH/$ROOT_WWW_FILE_NAME
sed -i "s|$PROVIDER_ID_PATTERN|$PROVIDER_ID|g" $APACHE_DIR_PATH/$ROOT_WWW_FILE_NAME
sed -i "s|$AS_PORT_PATTERN|$AS_PORT|g" $APACHE_DIR_PATH/$ROOT_WWW_FILE_NAME
sed -i "s|$RAS_PORT_PATTERN|$RAS_PORT|g" $APACHE_DIR_PATH/$ROOT_WWW_FILE_NAME
## Copying Shibboleth configuration (if required)
SHIB_CONF_FILE_NAME="shibboleth.conf"
SHIB_ENV_DIR_PATH=$COMMON_TEMPLATES_DIR_PATH/"shibboleth-environment"
if [ "$AT" == "shibboleth" ]; then
  SHIBBOLETH_SERVICE_PROVIDER_CRT_FILE_PATH=$CONF_FILES_DIR_PATH/"certs/shibboleth_service_provider.crt"
  SHIBBOLETH_SERVICE_PROVIDER_KEY_FILE_PATH=$CONF_FILES_DIR_PATH/"certs/shibboleth_service_provider.key"
  yes | cp -f $SHIBBOLETH_SERVICE_PROVIDER_CRT_FILE_PATH $APACHE_DIR_PATH
  yes | cp -f $SHIBBOLETH_SERVICE_PROVIDER_KEY_FILE_PATH $APACHE_DIR_PATH
  echo "# Shibboleth specific properties" > $APACHE_DIR_PATH/$SHIB_CONF_FILE_NAME
  echo $DSP_PATTERN=$DSP >> $APACHE_DIR_PATH/$SHIB_CONF_FILE_NAME
  echo $DSU_PATTERN=$DSU >> $APACHE_DIR_PATH/$SHIB_CONF_FILE_NAME
  echo $DSMU_PATTERN=$DSMU >> $APACHE_DIR_PATH/$SHIB_CONF_FILE_NAME
  ### Fill apache+shibboleth mod
  SHIB_VIRTUAL_HOST_80_FILE_NAME="default.conf"
  yes | cp -f $SHIB_ENV_DIR_PATH/$SHIB_VIRTUAL_HOST_80_FILE_NAME'.example' $APACHE_DIR_PATH/$SHIB_VIRTUAL_HOST_80_FILE_NAME
  SHIB_VIRTUAL_HOST_443_FILE_NAME="shibboleth-sp2.conf"
  yes | cp -f $SHIB_ENV_DIR_PATH/$SHIB_VIRTUAL_HOST_443_FILE_NAME'.example' $APACHE_DIR_PATH/$SHIB_VIRTUAL_HOST_443_FILE_NAME
  SHIB_XML_FILE_NAME="shibboleth2.xml"
  yes | cp -f $SHIB_ENV_DIR_PATH/$SHIB_XML_FILE_NAME'.example' $APACHE_DIR_PATH/$SHIB_XML_FILE_NAME
  ATTRIBUTE_MAP_XML_FILE_NAME="attribute-map.xml"
  yes | cp -f $SHIB_ENV_DIR_PATH/$ATTRIBUTE_MAP_XML_FILE_NAME'.example' $APACHE_DIR_PATH/$ATTRIBUTE_MAP_XML_FILE_NAME
  ATTRIBUTE_POLICY_XML_FILE_NAME="attribute-policy.xml"
  yes | cp -f $SHIB_ENV_DIR_PATH/$ATTRIBUTE_POLICY_XML_FILE_NAME'.example' $APACHE_DIR_PATH/$ATTRIBUTE_POLICY_XML_FILE_NAME
  INDEX_SECURE_HTML_FILE_NAME="index-secure.html"
  yes | cp -f $SHIB_ENV_DIR_PATH/$INDEX_SECURE_HTML_FILE_NAME'.example' $APACHE_DIR_PATH/$INDEX_SECURE_HTML_FILE_NAME
  ### default.conf
  HOSTNAME_PATTERN="_HOSTNAME_"
  sed -i "s#$HOSTNAME_PATTERN#$DSP#" $APACHE_DIR_PATH/$SHIB_VIRTUAL_HOST_80_FILE_NAME
  ### shibboleth-sp2.conf
  sed -i "s#$HOSTNAME_PATTERN#$DSP#" $APACHE_DIR_PATH/$SHIB_VIRTUAL_HOST_443_FILE_NAME
  SHIB_AUTHENTICATION_APPLICATION_ADDRESS_PATTERN="_ADDRESS_SHIBBOLETH_AUTH_APPLICATION_"
  SHIB_AUTHENTICATION_APPLICATION_ADDRESS_DEFAULT_VALUE="127.0.0.1:9000"
  sed -i "s#$SHIB_AUTHENTICATION_APPLICATION_ADDRESS_PATTERN#$SHIB_AUTHENTICATION_APPLICATION_ADDRESS_DEFAULT_VALUE#" $APACHE_DIR_PATH/$SHIB_VIRTUAL_HOST_443_FILE_NAME
  ### shibboleth2.xml
  sed -i "s#$HOSTNAME_PATTERN#$DSP#" $APACHE_DIR_PATH/$SHIB_XML_FILE_NAME
  DS_META_PATTERN="_DS_META_"
  sed -i "s#$DS_META_PATTERN#$DSMU#" $APACHE_DIR_PATH/$SHIB_XML_FILE_NAME
  DS_PATTERN="_DS_"
  sed -i "s#$DS_PATTERN#$DSU#" $APACHE_DIR_PATH/$SHIB_XML_FILE_NAME
  ### Fill apache+shibboleth mod
  LOG4J_PROPERTIES_FILE_NAME="log4j.properties"
  yes | cp -f $SHIB_ENV_DIR_PATH/$LOG4J_PROPERTIES_FILE_NAME'.example' $APACHE_DIR_PATH/$LOG4J_PROPERTIES_FILE_NAME
  SHIB_AUTHENTICATION_APPLICATION_PROPERTIES_FILE_NAME="shibboleth-authentication-application.conf"
  yes | cp -f $SHIB_ENV_DIR_PATH/$SHIB_AUTHENTICATION_APPLICATION_PROPERTIES_FILE_NAME'.example' $APACHE_DIR_PATH/$SHIB_AUTHENTICATION_APPLICATION_PROPERTIES_FILE_NAME
  SHIB_AUTH_APP_FOGBOW_GUI_URL_PATTERN="fogbow_gui_url="
  FOGBOW_GUI_URL="https://"$PROVIDER_ID/
  sed -i "s#$SHIB_AUTH_APP_FOGBOW_GUI_URL_PATTERN.*#$SHIB_AUTH_APP_FOGBOW_GUI_URL_PATTERN$FOGBOW_GUI_URL#" $APACHE_DIR_PATH/$SHIB_AUTHENTICATION_APPLICATION_PROPERTIES_FILE_NAME
  SHIB_AUTH_APP_SHIB_HTTP_PORT_PATTERN="shib_http_port="
  SHIB_HTTP_PORT="9000"
  sed -i "s#$SHIB_AUTH_APP_SHIB_HTTP_PORT_PATTERN.*#$SHIB_AUTH_APP_SHIB_HTTP_PORT_PATTERN$SHIB_HTTP_PORT#" $APACHE_DIR_PATH/$SHIB_AUTHENTICATION_APPLICATION_PROPERTIES_FILE_NAME
  SHIB_AUTH_APP_SERVICE_PROVIDER_MACHINE_IP_PATTERN="service_provider_machine_ip="
  SERVICE_PROVIDER_MACHINE_IP=127.0.0.1
  sed -i "s#$SHIB_AUTH_APP_SERVICE_PROVIDER_MACHINE_IP_PATTERN.*#$SHIB_AUTH_APP_SERVICE_PROVIDER_MACHINE_IP_PATTERN$SERVICE_PROVIDER_MACHINE_IP#" $APACHE_DIR_PATH/$SHIB_AUTHENTICATION_APPLICATION_PROPERTIES_FILE_NAME
  ### Generate shib app key pair
  SHIB_RSA_PEM_FILE_NAME="rsa_key_shibboleth.pem"
  SHIB_PRIVATE_KEY_FILE_NAME="shibboleth-app.pri"
  SHIB_PUBLIC_KEY_FILE_NAME="shibboleth-app.pub"
  openssl genrsa -out $APACHE_DIR_PATH/$SHIB_RSA_PEM_FILE_NAME 1024
  openssl pkcs8 -topk8 -in $APACHE_DIR_PATH/$SHIB_RSA_PEM_FILE_NAME -out $APACHE_DIR_PATH/$SHIB_PRIVATE_KEY_FILE_NAME -nocrypt
  openssl rsa -in $APACHE_DIR_PATH/$SHIB_PRIVATE_KEY_FILE_NAME -outform PEM -pubout -out $APACHE_DIR_PATH/$SHIB_PUBLIC_KEY_FILE_NAME
  chmod 600 $APACHE_DIR_PATH/$SHIB_PRIVATE_KEY_FILE_NAME
  rm $APACHE_DIR_PATH/$SHIB_RSA_PEM_FILE_NAME
  ### Copy shib public key to AS conf-files dir
  yes | cp -f $APACHE_DIR_PATH/$SHIB_PUBLIC_KEY_FILE_NAME $AS_DIR_PATH
  echo "shib_public_key_file_path="$AS_CONTAINER_CONF_FILE_DIR_PATH/$SHIB_PUBLIC_KEY_FILE_NAME >> $AS_DIR_PATH/$AS_CONF_FILE_NAME
fi
