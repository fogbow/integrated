FROM node:8

RUN \
  sed -i 's/# \(.*multiverse$\)/\1/g' /etc/apt/sources.list && \
  apt-get update -y && \
  apt-get upgrade -y && \
  apt-get install -y byobu curl git htop man unzip vim nano wget && \
  apt-get install -y net-tools iputils-ping

# Adding dependencies
ADD . /root/fogbow-gui  

# Define working directory.
WORKDIR /root/fogbow-gui

# Generates the build number based on the commit checksum
RUN \
  (build_number=test && echo "{\"build_number\": \"$build_number\"}" > /root/fogbow-gui/src/defaults/build.json)

RUN \
  npm i

CMD \
  npm start > fogbow-gui.log || tail -f /dev/null