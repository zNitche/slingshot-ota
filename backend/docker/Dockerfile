FROM python:3.13

ARG USERNAME=slingshot_user
ARG USER_UID=1111
ARG USER_GID=${USER_UID}

# Add non root user
RUN groupadd --gid ${USER_GID} ${USERNAME}
RUN useradd --uid ${USER_UID} --gid ${USER_GID} -s /bin/bash -m ${USERNAME}

# Setup container user
USER ${USERNAME}

COPY . /slingshot_be

WORKDIR /slingshot_be

RUN pip3 install -r requirements/common.txt
RUN pip3 install -r requirements/deployment.txt
