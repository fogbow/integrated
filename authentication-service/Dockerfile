FROM fogbow/common:latest

# Adding dependencies
ADD . /root/authentication-service

# Define working directory.
WORKDIR /root/authentication-service

# Generates the build number based on the commit checksum
RUN as_build_number=test && \
    common_build_number=test && \
    cd /root/authentication-service && \
    echo "build_number=as-$as_build_number-common-$common_build_number" > build

RUN \
  mvn dependency:sources
  