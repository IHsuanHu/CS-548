FROM payara/micro:6.2023.2-jdk17
COPY --chown=payara:payara clinic-domain.war ${DEPLOY_DIR}
CMD [ "--contextroot", "api", "--deploy", "/opt/payara/deployments/clinic-domain.war" ]