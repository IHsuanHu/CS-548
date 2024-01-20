FROM payara/micro:6.2023.6-jdk17
COPY --chown=payara:payara chat-webapp.war ${DEPLOY_DIR}
CMD [ "--contextroot", "chat", "--deploy", "/opt/payara/deployments/chat-webapp.war" ]