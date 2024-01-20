FROM payara/micro:6.2023.9-jdk17
COPY --chown=payara:payara clinic-webapp.war ${DEPLOY_DIR}
CMD [ "--contextroot", "clinic","--deploy", "/opt/payara/deployments/clinic-webapp.war" ]
ENV JVM_ARGS="--add-opens=java.base/java.io=ALL-UNNAMED"