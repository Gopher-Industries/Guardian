# **CIS Docker Benchmark Checklist**

| # | CIS Benchmark Recommendation | Set Correctly - Yes | Set Correctly - No | N/A|
|----------|--------------------|-----------------------|------------------------|------------------------|
| **1** | **Host Configuration** |
| 1.1 | Linux Hosts Specific Configuration |  |  |  |
| 1.1.1 | Ensure a separate partition for containers has been created (Manual) |  |  |  |
| 1.1.2 | Ensure only trusted users are allowed to control Docker daemon (Manual) |  |  |  |
| 1.1.3 | Ensure auditing is configured for the Docker daemon (Automated) |  |  |  |
| 1.1.4 | Ensure auditing is configured for Docker files and directories - /run/containerd (Automated) |  |  |  |
| 1.1.5 | Ensure auditing is configured for Docker files and directories - /var/lib/docker (Manual) |  |  |  |
| 1.1.6 | Ensure auditing is configured for Docker files and directories - /etc/docker (Automated) |  |  |  |
| 1.1.7 | Ensure auditing is configured for Docker files and directories - docker.service (Automated) |  |  |  |
| 1.1.8 | Ensure auditing is configured for Docker files and directories - containerd.sock (Automated) |  |  |  |
| 1.1.9 | Ensure auditing is configured for Docker files and directories - docker.socket (Automated) |  |  |  |
| 1.1.10 | Ensure auditing is configured for Docker files and directories - /etc/default/docker (Automated) |  |  |  |
| 1.1.11 | Ensure auditing is configured for Docker files and directories - /etc/docker/daemon.json (Automated) |  |  |  |
| 1.1.12 | Ensure auditing is configured for Docker files and directories - /etc/containerd/config.toml (Automated) |  |  |  |
| 1.1.13 | Ensure auditing is configured for Docker files and directories - /etc/sysconfig/docker (Automated) |  |  |  |
| 1.1.14 | Ensure auditing is configured for Docker files and directories - /usr/bin/containerd (Automated) |  |  |  |
| 1.1.15 | Ensure auditing is configured for Docker files and directories - /usr/bin/containerd-shim (Manual) |  |  |  |
| 1.1.16 | Ensure auditing is configured for Docker files and directories - /usr/bin/containerd-shim-runc-v1 (Manual) |  |  |  |
| 1.1.17 | Ensure auditing is configured for Docker files and directories - /usr/bin/containerd-shim-runc-v2 (Manual) |  |  |  |
| 1.1.18 | Ensure auditing is configured for Docker files and directories - /usr/bin/runc (Manual) |  |  |  |
| 1.2 | General Configuration |  |  |  |
| 1.2.1 | Ensure the container host has been Hardened (Manual) |  |  |  |
| 1.2.2 | Ensure that the version of Docker is up to date (Manual) |  |  |  |
| **2** | **Docker daemon configuration** |
| 2.1 | Run the Docker daemon as a non-root user, if possible (Manual) |  |  |  |
| 2.2 | Ensure network traffic is restricted between containers on the default bridge (Manual) |  |  |  |
| 2.3 | Ensure the logging level is set to 'info' (Manual) |  |  |  |
| 2.4 | Ensure Docker is allowed to make changes to iptables (Manual) |  |  |  |
| 2.5 | Ensure insecure registries are not used (Manual) |  |  |  |
| 2.6 | Ensure aufs storage driver is not used (Manual) |  |  |  |
| 2.7 | Ensure TLS authentication for Docker daemon is configured (Manual) |  |  |  |
| 2.8 | Ensure the default ulimit is configured appropriately (Manual) |  |  |  |
| 2.9 | Enable user namespace support (Manual) |  |  |  |
| 2.10 | Ensure the default cgroup usage has been confirmed (Manual) |  |  |  |
| 2.11 | Ensure base device size is not changed until needed (Manual) |  |  |  |
| 2.12 | Ensure that authorization for Docker client commands is enabled (Manual) |  |  |  |
| 2.13 | Ensure centralized and remote logging is configured (Manual) |  |  |  |
| 2.14 | Ensure containers are restricted from acquiring new privileges (Manual) |  |  |  |
| 2.15 | Ensure live restore is enabled (Manual) |  |  |  |
| 2.16 | Ensure Userland Proxy is Disabled (Manual) |  |  |  |
| 2.17 | Ensure that a daemon-wide custom seccomp profile is applied if appropriate (Manual) |  |  |  |
| 2.18 | Ensure that experimental features are not implemented in production (Manual) |  |  |  |
| **3** | **Docker daemon configuration files** |
| 3.1 | Ensure that the docker.service file ownership is set to root:root (Automated) |  |  |  |
| 3.2 | Ensure that docker.service file permissions are appropriately set (Automated) |  |  |  |
| 3.3 | Ensure that docker.socket file ownership is set to root:root (Automated) |  |  |  |
| 3.4 | Ensure that docker.socket file permissions are set to 644 or more restrictive (Automated) |  |  |  |
| 3.5 | Ensure that the /etc/docker directory ownership is set to root:root (Automated) |  |  |  |
| 3.6 | Ensure that /etc/docker directory permissions are set to 755 or more restrictively (Automated) |  |  |  |
| 3.7 | Ensure that registry certificate file ownership is set to root:root (Manual) |  |  |  |
| 3.8 | Ensure that registry certificate file permissions are set to 444 or more restrictively (Manual) |  |  |  |
| 3.9 | Ensure that TLS CA certificate file ownership is set to root:root (Manual) |  |  |  |
| 3.10 | Ensure that TLS CA certificate file permissions are set to 444 or more restrictively (Manual) |  |  |  |
| 3.11 | Ensure that Docker server certificate file ownership is set to root:root (Manual) |  |  |  |
| 3.12 | Ensure that the Docker server certificate file permissions are set to 444 or more restrictively (Manual) |  |  |  |
| 3.13 | Ensure that the Docker server certificate key file ownership is set to root:root (Manual) |  |  |  |
| 3.14 | Ensure that the Docker server certificate key file permissions are set to 400 (Manual) |  |  |  |
| 3.15 | Ensure that the Docker socket file ownership is set to root:docker (Automated) |  |  |  |
| 3.16 | Ensure that the Docker socket file permissions are set to 660 or more restrictively (Automated) |  |  |  |
| 3.17 | Ensure that the daemon.json file ownership is set to root:root (Manual) |  |  |  |
| 3.18 | Ensure that daemon.json file permissions are set to 644 or more restrictive (Manual) |  |  |  |
| 3.19 | Ensure that the /etc/default/docker file ownership is set to root:root (Manual) |  |  |  |
| 3.20 | Ensure that the /etc/default/docker file permissions are set to 644 or more restrictively (Manual) |  |  |  |
| 3.21 | Ensure that the /etc/sysconfig/docker file permissions are set to 644 or more restrictively (Manual) |  |  |  |
| 3.22 | Ensure that the /etc/sysconfig/docker file ownership is set to root:root (Manual) |  |  |  |
| 3.23 | Ensure that the Containerd socket file ownership is set to root:root (Automated) |  |  |  |
| 3.24 | Ensure that the Containerd socket file permissions are set to 660 or more restrictively (Automated) |  |  |  |
| **4** | **Container Images and Build File Configuration** |
| 4.1 | Ensure that a user for the container has been created (Manual) |  |  |  |
| 4.2 | Ensure that containers use only trusted base images (Manual) |  |  |  |
| 4.3 | Ensure that unnecessary packages are not installed in the container (Manual) |  |  |  |
| 4.4 | Ensure images are scanned and rebuilt to include security patches (Manual) |  |  |  |
| 4.5 | Ensure Content trust for Docker is Enabled (Manual) |  |  |  |
| 4.6 | Ensure that HEALTHCHECK instructions have been added to container images (Manual) |  |  |  |
| 4.7 | Ensure update instructions are not used alone in Dockerfiles (Manual) |  |  |  |
| 4.8 | Ensure setuid and setgid permissions are removed (Manual) |  |  |  |
| 4.9 | Ensure that COPY is used instead of ADD in Dockerfiles (Manual) |  |  |  |
| 4.10 | Ensure secrets are not stored in Dockerfiles (Manual) |  |  |  |
| 4.11 | Ensure only verified packages are installed (Manual) |  |  |  |
| 4.12 | Ensure all signed artifacts are validated (Manual) |  |  |  |
| **5** | **Container Runtime Configuration** |
| 5.1 | Ensure swarm mode is not Enabled, if not needed (Manual) |  |  |  |
| 5.2 | Ensure that, if applicable, an AppArmor Profile is enabled (Manual) |  |  |  |
| 5.3 | Ensure that, if applicable, SELinux security options are set (Manual) |  |  |  |
| 5.4 | Ensure that Linux kernel capabilities are restricted within containers (Manual) |  |  |  |
| 5.5 | Ensure that privileged containers are not used (Manual) |  |  |  |
| 5.6 | Ensure sensitive host system directories are not mounted on containers (Manual) |  |  |  |
| 5.7 | Ensure sshd is not run within containers (Manual) |  |  |  |
| 5.8 | Ensure privileged ports are not mapped within containers (Manual) |  |  |  |
| 5.9 | Ensure that only needed ports are open on the container (Manual) |  |  |  |
| 5.10 | Ensure that the host's network namespace is not shared (Manual) |  |  |  |
| 5.11 | Ensure that the memory usage for containers is limited (Manual) |  |  |  |
| 5.12 | Ensure that CPU priority is set appropriately on containers (Manual) |  |  |  |
| 5.13 | Ensure that the container's root filesystem is mounted as read only (Manual) |  |  |  |
| 5.14 | Ensure that incoming container traffic is bound to a specific host interface (Manual) |  |  |  |
| 5.15 | Ensure that the 'on-failure' container restart policy is set to '5' (Manual) |  |  |  |
| 5.16 | Ensure that the host's process namespace is not shared (Manual) |  |  |  |
| 5.17 | Ensure that the host's IPC namespace is not shared (Manual) |  |  |  |
| 5.18 | Ensure that host devices are not directly exposed to containers (Manual) |  |  |  |
| 5.19 | Ensure that the default ulimit is overwritten at runtime if needed (Manual) |  |  |  |
| 5.20 | Ensure mount propagation mode is not set to shared (Manual) |  |  |  |
| 5.21 | Ensure that the host's UTS namespace is not shared (Manual) |  |  |  |
| 5.22 | Ensure the default seccomp profile is not Disabled (Manual) |  |  |  |
| 5.23 | Ensure that docker exec commands are not used with the privileged option (Manual) |  |  |  |
| 5.24 | Ensure that docker exec commands are not used with the user=root option (Manual) |  |  |  |
| 5.25 | Ensure that cgroup usage is confirmed (Manual) |  |  |  |
| 5.26 | Ensure that the container is restricted from acquiring additional privileges (Manual) |  |  |  |
| 5.27 | Ensure that container health is checked at runtime (Manual) |  |  |  |
| 5.28 | Ensure that Docker commands always make use of the latest version of their image (Manual) |  |  |  |
| 5.29 | Ensure that the PIDs cgroup limit is used (Manual) |  |  |  |
| 5.30 | Ensure that Docker's default bridge "docker0" is not used (Manual) |  |  |  |
| 5.31 | Ensure that the host's user namespaces are not shared (Manual) |  |  |  |
| 5.32 | Ensure that the Docker socket is not mounted inside any containers (Manual) |  |  |  |
| **6** | **Docker Security Operations** |
| 6.1 | Ensure that image sprawl is avoided (Manual) |  |  |  |
| 6.2 | Ensure that container sprawl is avoided (Manual) |  |  |  |
| **7** | **Docker Swarm Configurations** |
| 7.1 | Ensure that the minimum number of manager nodes have been created in a swarm (Manual) |  |  |  |
| 7.2 | Ensure that swarm services are bound to a specific host interface (Manual) |  |  |  |
| 7.3 | Ensure that all Docker swarm overlay networks are encrypted (Manual) |  |  |  |
| 7.4 | Ensure that Docker's secret management commands are used for managing secrets in a swarm cluster (Manual) |  |  |  |
| 7.5 | Ensure that swarm manager is run in auto-lock mode (Manual) |  |  |  |
| 7.6 | Ensure that the swarm manager auto-lock key is rotated periodically (Manual) |  |  |  |
| 7.7 | Ensure that node certificates are rotated as appropriate (Manual) |  |  |  |
| 7.8 | Ensure that CA certificates are rotated as appropriate (Manual) |  |  |  |
| 7.9 | Ensure that management plane traffic is separated from data plane traffic (Manual) |  |  |  |

___
## References
[1] Center for Internet Security, "CIS Docker Benchmark," <em>Center for Internet Security</em>, 2023.
___
###### _Emily Merchant, 8th December, 2023_