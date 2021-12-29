FROM fogbow/common:latest

# Adding dependencies
ADD dependencies/fogbow-as /root/.m2/repository/fogbow/fogbow-as
ADD dependencies/fogbow-ras /root/.m2/repository/fogbow/fogbow-ras
ADD dependencies/fogbow-ms /root/.m2/repository/fogbow/fogbow-ms
ADD dependencies/fogbow-accounting-service /root/.m2/repository/fogbow/fogbow-accounting-service
ADD . /root/finance-service

# Define working directory.
WORKDIR /root/finance-service

# Generates the build number based on the commit checksum
RUN \
    fs_build_number=test && \
   	ms_build_number=test && \
    accs_build_number=test && \
    as_build_number=test && \
    common_build_number=test && \
    cd /root/finance-service && \
    echo "build_number=fs-$fs_build_number-ms-$ms_build_number-accs-$accs_build_number-as-$as_build_number-common-$common_build_number" > build

RUN \
  mvn dependency:sources
