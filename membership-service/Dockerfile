FROM fogbow/common:latest

# Adding dependencies
ADD dependencies/fogbow-as /root/.m2/repository/fogbow/fogbow-as
ADD . /root/membership-service

# Define working directory.
WORKDIR /root/membership-service

# Generates the build number based on the commit checksum
RUN ms_build_number=test && \
	as_build_number=test && \
    common_build_number=test && \
    cd /root/membership-service && \
    echo "build_number=ms-$ms_build_number-as-$as_build_number-common-$common_build_number" > build

RUN \
  mvn dependency:sources
