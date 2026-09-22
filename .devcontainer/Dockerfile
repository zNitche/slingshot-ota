FROM node:24.11.1-slim

ARG USERNAME=slingshot_node
ARG USER_UID=1111
ARG USER_GID=${USER_UID}

# Add non root user
RUN groupadd --gid ${USER_GID} ${USERNAME}
RUN useradd --uid ${USER_UID} --gid ${USER_GID} -s /bin/bash -m ${USERNAME}

# Container setup
RUN apt update && apt install -y git

# Setup container user
USER ${USERNAME}

# Done
ENTRYPOINT [ "/bin/bash" ]