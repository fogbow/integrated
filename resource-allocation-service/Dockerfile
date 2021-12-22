FROM fogbow/common:latest

# Adding dependencies
ADD dependencies/fogbow-as /root/.m2/repository/fogbow/fogbow-as
ADD dependencies/fogbow-ms /root/.m2/repository/fogbow/fogbow-ms
ADD . /root/resource-allocation-service

# Define working directory.
WORKDIR /root/resource-allocation-service

# Generates the build number based on the commit checksum
RUN ras_build_number=test && \
    as_build_number=test && \
    common_build_number=test && \
    ms_build_number=test && \
    cd /root/resource-allocation-service && \
    echo "build_number=ras-$ras_build_number-as-$as_build_number-ms-$ms_build_number-common-$common_build_number" > build

RUN \
  mvn dependency:sources
