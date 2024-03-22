# **CIS GitHub Benchmark Checklist**

| # | CIS Benchmark Recommendation | Set Correctly - Yes | Set Correctly - No | N/A|
|----------|--------------------|-----------------------|------------------------|------------------------|
| **1** | **Source Code** |
| **1.1** | **Code Changes** |  |  |  |
| 1.1.1 | Ensure any changes to code are tracked in a version control platform (Manual) |  |  |  |
| 1.1.2 | Ensure any change to code can be traced back to its associated task (Manual) |  |  |  |
| 1.1.3 | Ensure any change to code receives approval of two strongly authenticated users (Automated) |  |  |  |
| 1.1.4 | Ensure previous approvals are dismissed when updates are introduced to a code change proposal (Manual) |  |  |  |
| 1.1.5 | Ensure there are restrictions on who can dismiss code change reviews (Manual) |  |  |  |
| 1.1.6 | Ensure code owners are set for extra sensitive code or configuration (Manual) |  |  |  |
| 1.1.7 | Ensure code owner's review is required when a change affects owned code (Manual) |  |  |  |
| 1.1.8 | Ensure inactive branches are periodically reviewed and removed (Manual) |  |  |  |
| 1.1.9 | Ensure all checks have passed before merging new code (Manual) |  |  |  |
| 1.1.10 | Ensure open Git branches are up to date before they can be merged into code base (Manual) |  |  |  |
| 1.1.11 | Ensure all open comments are resolved before allowing code change merging (Manual) |  |  |  |
| 1.1.12 | Ensure verification of signed commits for new changes before merging (Manual) |  |  |  |
| 1.1.13 | Ensure linear history is required (Manual) |  |  |  |
| 1.1.14 | Ensure branch protection rules are enforced for administrators (Manual) |  |  |  |
| 1.1.15 | Ensure pushing or merging of new code is restricted to specific individuals or teams (Manual) |  |  |  |
| 1.1.16 | Ensure force push code to branches is denied (Manual) |  |  |  |
| 1.1.17 | Ensure branch deletions are denied (Manual) |  |  |  |
| 1.1.18 | Ensure any merging of code is automatically scanned for risks (Manual) |  |  |  |
| 1.1.19 | Ensure any changes to branch protection rules are audited (Manual) |  |  |  |
| 1.1.20 | Ensure branch protection is enforced on the default branch (Manual) |  |  |  |
| **1.2** | **Repository Management** |
| 1.2.1 | Ensure all public repositories contain a SECURITY.md file (Manual) |  |  |  |
| 1.2.2 | Ensure repository creation is limited to specific members (Manual) |  |  |  |
| 1.2.3 | Ensure repository deletion is limited to specific users (Manual) |  |  |  |
| 1.2.4 | Ensure issue deletion is limited to specific users (Manual) |  |  |  |
| 1.2.5 | Ensure all copies (forks) of code are tracked and accounted for (Manual) |  |  |  |
| 1.2.6 | Ensure all code projects are tracked for changes in visibility status (Manual) |  |  |  |
| 1.2.7 | Ensure inactive repositories are reviewed and archived periodically (Manual) |  |  |  |
| **1.3** | **Contribution Access** |
| 1.3.1 | Ensure inactive users are reviewed and removed periodically (Manual) |  |  |  |
| 1.3.2 | Ensure team creation is limited to specific members (Manual) |  |  |  |
| 1.3.3 | Ensure minimum number of administrators are set for the organization (Manual) |  |  |  |
| 1.3.4 | Ensure Multi-Factor Authentication (MFA) is required for contributors of new code (Manual) |  |  |  |
| 1.3.5 | Ensure the organization is requiring members to use Multi-Factor Authentication (MFA) (Manual) |  |  |  |
| 1.3.6 | Ensure new members are required to be invited using company-approved email (Manual) |  |  |  |
| 1.3.7 | Ensure two administrators are set for each repository (Manual) |  |  |  |
| 1.3.8 | Ensure strict base permissions are set for repositories (Manual) |  |  |  |
| 1.3.9 | Ensure an organization's identity is confirmed with a "Verified" badge (Manual) |  |  |  |
| 1.3.10 | Ensure Source Code Management (SCM) email notifications are restricted to verified domains (Manual) |  |  |  |
| 1.3.11 | Ensure an organization provides SSH certificates (Manual) |  |  |  |
| 1.3.12 | Ensure Git access is limited based on IP addresses (Manual) |  |  |  |
| 1.3.13 | Ensure anomalous code behavior is tracked (Manual) |  |  |  |
| **1.4** | **Third-Party** |
| 1.4.1 | Ensure administrator approval is required for every installed application (Manual) |  |  |  |
| 1.4.2 | Ensure stale applications are reviewed and inactive ones are removed (Manual) |  |  |  |
| 1.4.3 | Ensure the access granted to each installed application is limited to the least privilege needed (Manual) |  |  |  |
| 1.4.4 | Ensure only secured webhooks are used (Manual) |  |  |  |
| **1.5** | **Code Risks** |
| 1.5.1 | Ensure scanners are in place to identify and prevent sensitive data in code (Manual) |  |  |  |
| 1.5.2 | Ensure scanners are in place to secure Continuous Integration (CI) pipeline instructions (Manual) |  |  |  |
| 1.5.3 | Ensure scanners are in place to secure Infrastructure as Code (IaC) instructions (Manual) |  |  |  |
| 1.5.4 | Ensure scanners are in place for code vulnerabilities (Manual) |  |  |  |
| 1.5.5 | Ensure scanners are in place for open-source vulnerabilities in used packages (Manual) |  |  |  |
| 1.5.6 | Ensure scanners are in place for open-source license issues in used packages (Manual) |  |  |  |
| **2** | **Build Pipelines** |
| **2.1** | **Build Environment** |
| 2.1.1 | Ensure each pipeline has a single responsibility (Manual) |  |  |  |
| 2.1.2 | Ensure all aspects of the pipeline infrastructure and configuration are immutable (Manual) |  |  |  |
| 2.1.3 | Ensure the build environment is logged (Manual) |  |  |  |
| 2.1.4 | Ensure the creation of the build environment is automated (Manual) |  |  |  |
| 2.1.5 | Ensure access to build environments is limited (Manual) |  |  |  |
| 2.1.6 | Ensure users must authenticate to access the build environment (Manual) |  |  |  |
| 2.1.7 | Ensure build secrets are limited to the minimal necessary scope (Manual) |  |  |  |
| 2.1.8 | Ensure the build infrastructure is automatically scanned for vulnerabilities (Manual) |  |  |  |
| 2.1.9 | Ensure default passwords are not used (Manual) |  |  |  |
| 2.1.10 | Ensure webhooks of the build environment are secured (Manual) |  |  |  |
| 2.1.11 | Ensure minimum number of administrators are set for the build environment (Manual) |  |  |  |
| **2.2** | **Build Worker** |
| 2.2.1 | Ensure build workers are single-used (Manual) |  |  |  |
| 2.2.2 | Ensure build worker environments and commands are passed and not pulled (Manual) |  |  |  |
| 2.2.3 | Ensure the duties of each build worker are segregated (Manual) |  |  |  |
| 2.2.4 | Ensure build workers have minimal network connectivity (Manual) |  |  |  |
| 2.2.5 | Ensure run-time security is enforced for build workers (Manual) |  |  |  |
| 2.2.6 | Ensure build workers are automatically scanned for vulnerabilities (Manual) |  |  |  |
| 2.2.7 | Ensure build workers' deployment configuration is stored in a version control platform (Manual) |  |  |  |
| 2.2.8 | Ensure resource consumption of build workers is monitored (Manual) |  |  |  |
| **2.3** | **Pipeline Instructions** |
| 2.3.1 | Ensure all build steps are defined as code (Manual) |  |  |  |
| 2.3.2 | Ensure steps have clearly defined build stage input and output (Manual) |  |  |  |
| 2.3.3 | Ensure output is written to a separate, secured storage repository (Manual) |  |  |  |
| 2.3.4 | Ensure changes to pipeline files are tracked and reviewed (Manual) |  |  |  |
| 2.3.5 | Ensure access to build process triggering is minimized (Manual) |  |  |  |
| 2.3.6 | Ensure pipelines are automatically scanned for misconfigurations (Manual) |  |  |  |
| 2.3.7 | Ensure pipelines are automatically scanned for vulnerabilities (Manual) |  |  |  |
| 2.3.8 | Ensure scanners are in place to identify and prevent sensitive data in pipeline files (Automated) |  |  |  |
| **2.4** | **Pipeline Integrity** |
| 2.4.1 | Ensure all artifacts on all releases are signed (Manual) |  |  |  |
| 2.4.2 | Ensure all external dependencies used in the build process are locked (Manual) |  |  |  |
| 2.4.3 | Ensure dependencies are validated before being used (Manual) |  |  |  |
| 2.4.4 | Ensure the build pipeline creates reproducible artifacts (Manual) |  |  |  |
| 2.4.5 | Ensure pipeline steps produce a Software Bill of Materials (SBOM) (Manual) |  |  |  |
| 2.4.6 | Ensure pipeline steps sign the Software Bill of Materials (SBOM) produced (Manual) |  |  |  |
| **3** | **Dependencies** |
| **3.1** | **Third-Party Packages** |
| 3.1.1 | Ensure third-party artifacts and open-source libraries are verified (Manual) |  |  |  |
| 3.1.2 | Ensure Software Bill of Materials (SBOM) is required from all third-party suppliers (Manual) |  |  |  |
| 3.1.3 | Ensure signed metadata of the build process is required and verified (Manual) |  |  |  |
| 3.1.4 | Ensure dependencies are monitored between opensource components (Manual) |  |  |  |
| 3.1.5 | Ensure trusted package managers and repositories are defined and prioritized (Manual) |  |  |  |
| 3.1.6 | Ensure a signed Software Bill of Materials (SBOM) of the code is supplied (Manual) |  |  |  |
| 3.1.7 | Ensure dependencies are pinned to a specific, verified version (Manual) |  |  |  |
| 3.1.8 | Ensure all packages used are more than 60 days old (Manual) |  |  |  |
| **3.2** | **Validate Packages** |
| 3.2.1 | Ensure an organization-wide dependency usage policy is enforced (Manual) |  |  |  |
| 3.2.2 | Ensure packages are automatically scanned for known vulnerabilities (Manual) |  |  |  |
| 3.2.3 | Ensure packages are automatically scanned for license implications (Manual) |  |  |  |
| 3.2.4 | Ensure packages are automatically scanned for ownership change (Manual) |  |  |  |
| **4** | **Artifacts** |
| **4.1** | **Verification** |
| 4.1.1 | Ensure all artifacts are signed by the build pipeline itself (Manual) |  |  |  |
| 4.1.2 | Ensure artifacts are encrypted before distribution (Manual) |  |  |  |
| 4.1.3 | Ensure only authorized platforms have decryption capabilities of artifacts (Manual) |  |  |  |
| **4.2** | **Access to Artifacts** |
| 4.2.1 | Ensure the authority to certify artifacts is limited (Manual) |  |  |  |
| 4.2.2 | Ensure number of permitted users who may upload new artifacts is minimized (Manual) |  |  |  |
| 4.2.3 | Ensure user access to the package registry utilizes Multi- Factor Authentication (MFA) (Manual) |  |  |  |
| 4.2.4 | Ensure user management of the package registry is not local (Manual) |  |  |  |
| 4.2.5 | Ensure anonymous access to artifacts is revoked (Manual) |  |  |  |
| 4.2.6 | Ensure minimum number of administrators are set for the package registry (Manual) |  |  |  |
| **4.3** | **Package Registries** |
| 4.3.1 | Ensure all signed artifacts are validated upon uploading the package registry (Manual) |  |  |  |
| 4.3.2 | Ensure all versions of an existing artifact have their signatures validated (Manual) |  |  |  |
| 4.3.3 | Ensure changes in package registry configuration are audited (Manual) |  |  |  |
| 4.3.4 | Ensure webhooks of the repository are secured (Manual) |  |  |  |
| **4.4** | **Origin Traceability** |
| 4.4.1 | Ensure artifacts contain information about their origin (Manual) |  |  |  |
| **5** | **Deployment** |
| **5.1** | **Deployment Configuration** |
| 5.1.1 | Ensure deployment configuration files are separated from source code (Manual) |  |  |  |
| 5.1.2 | Ensure changes in deployment configuration are audited (Manual) |  |  |  |
| 5.1.3 | Ensure scanners are in place to identify and prevent sensitive data in deployment configuration (Manual) |  |  |  |
| 5.1.4 | Limit access to deployment configurations (Manual) |  |  |  |
| 5.1.5 | Scan Infrastructure as Code (IaC) (Manual) |  |  |  |
| 5.1.6 | Ensure deployment configuration manifests are verified (Manual) |  |  |  |
| 5.1.7 | Ensure deployment configuration manifests are pinned to a specific, verified version (Manual) |  |  |  |
| **5.2** | **Deployment Environment** |
| 5.2.1 | Ensure deployments are automated (Manual) |  |  |  |
| 5.2.2 | Ensure the deployment environment is reproducible (Manual) |  |  |  |
| 5.2.3 | Ensure access to production environment is limited (Manual) |  |  |  |
| 5.2.4 | Ensure default passwords are not used (Manual) |  |  |  |

___
## References
[1] Center for Internet Security, "CIS GitHub Benchmark," <em>Center for Internet Security</em>, 2022.
___
###### _Emily Merchant, 8th December, 2023_