FROM airhacks/payara
MAINTAINER Marcus Fihlon, fihlon.ch
COPY target/sportchef.war ${DEPLOYMENT_DIR}

