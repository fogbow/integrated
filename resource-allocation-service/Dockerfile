FROM fogbow/common:latest
#FROM openjdk:8

# Install.
#RUN \
#  sed -i 's/# \(.*multiverse$\)/\1/g' /etc/apt/sources.list && \
#  apt-get update -y && \
#  apt-get upgrade -y && \
#  apt-get install -y build-essential && \
#  apt-get install -y software-properties-common && \
#  apt-get install -y byobu curl git htop man unzip vim wget maven && \
#  apt-get install -y net-tools iputils-ping && \
#  rm -rf /var/lib/apt/lists/*

# Set environment variables.
ENV HOME /root

# Define working directory.
WORKDIR /root

#ADD dependencies/common /root/.m2/repository/fogbow/common
ADD dependencies/fogbow-as /root/.m2/repository/fogbow/fogbow-as
ADD dependencies/fogbow-ms /root/.m2/repository/fogbow/fogbow-ms
ADD . /root/resource-allocation-service

# Installing Common
#RUN cd common && mvn install -Dmaven.test.skip=true

# Installing Authentication Service
#RUN cd authentication-service && mvn install -Dmaven.test.skip=true

# Installing Membership Service
#RUN cd membership-service && mvn install -Dmaven.test.skip=true

# Define working directory.
WORKDIR /root/resource-allocation-service

# Generates the build number based on the commit checksum
RUN ras_build_number=$(git rev-parse --short 'HEAD') \
    as_build_number=$(git rev-parse --short 'HEAD') \
    common_build_number=$(git rev-parse --short 'HEAD') \
    ms_build_number=$(git rev-parse --short 'HEAD') \
    cd /root/resource-allocation-service && \
    echo "build_number=ras-$ras_build_number-as-$as_build_number-ms-$ms_build_number-common-$common_build_number" > build

RUN \
  mvn dependency:sources

