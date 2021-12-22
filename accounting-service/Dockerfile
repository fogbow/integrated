FROM fogbow/common:latest

# Adding dependencies
ADD dependencies/fogbow-as /root/.m2/repository/fogbow/fogbow-as
ADD dependencies/fogbow-ras /root/.m2/repository/fogbow/fogbow-ras
ADD . /root/accounting-service

# Define working directory.
WORKDIR /root/accounting-service

# Generates the build number based on the commit checksum
RUN \
    accs_build_number=test && \
    as_build_number=test && \
    common_build_number=test && \
    cd /root/accounting-service && \
    echo "build_number=accs-$accs_build_number-as-$as_build_number-common-$common_build_number" > build

RUN \
  mvn dependency:sources
