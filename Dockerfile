FROM jboss/wildfly
ADD target/sportchef.war /opt/jboss/wildfly/standalone/deployments/
