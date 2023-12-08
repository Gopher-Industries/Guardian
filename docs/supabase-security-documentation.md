# Supabase Security Documentation

## Overview

Supabase is committed to providing a secure environment for developers to build and deploy applications. The security measures are designed to protect sensitive customer data and ensure compliance with industry standards. Below is an overview of key security features and practices implemented by Supabase.

## Compliance

### SOC 2 Type 2

Supabase adheres to SOC 2 Type 2 compliance standards. This ensures that all security policies and practices meet the necessary criteria when handling sensitive customer data. Enterprise and Teams customers can access our SOC 2 report on the dashboard.

### HIPAA

Supabase is HIPAA (Privacy Act 1988 equivalent) compliant, allowing the storage of Protected Health Information (PHI) on the Supabase hosted platform. To leverage this capability, customers need to enter into a Business Associate Agreement (BAA) with Supabase. Enterprise and Teams customers can request to sign the BAA through the dashboard.

## Data Encryption

All customer data is encrypted to ensure its confidentiality and integrity:

- **At Rest:** Data is encrypted at rest using AES-256.
- **In Transit:** Data is encrypted in transit via Transport Layer Security (TLS).

Sensitive information such as access tokens and keys are encrypted at the application level before being stored in the database, adding an additional layer of protection.

## GitHub Security Integration

Supabase has partnered with GitHub to enhance security:

- GitHub scans for Supabase service role API keys.
- If Supabase API keys are inadvertently pushed to GitHub, they are automatically revoked.

## Role-Based Access Control (RBAC)

Supabase provides Role-Based Access Control to manage and restrict access to specific resources. Members of organisations within Supabase can be granted access based on their roles.

## Backups

To ensure data resilience and availability:

- All paid customer databases are backed up daily.
- Point in Time Recovery allows customers on the Pro plan to restore the database to any specific point in time.

## Payment Processing

Supabase utilises Stripe for payment processing, ensuring a secure and certified environment:

- Stripe is a certified PCI Service Provider Level 1, the highest level of certification in the payments industry.
- Supabase does not store personal credit card information for any customers.

## Vulnerability Management

Supabase employs robust vulnerability management practices:

- Regular penetration tests are conducted by industry experts.
- Internal security reviews are performed regularly.
- Code is scanned for vulnerabilities using tools such as GitHub, Vanta, and Snyk.

Supabase commitment to security involves continuous improvement and collaboration with industry experts to stay ahead of potential threats. They prioritise the protection of data and the integrity of their platform to provide a secure development and deployment environment.

## Recommendations

1. **Use HTTPS:** Ensure that your Supabase connection is secured using HTTPS. This encrypts the data transmitted between your application and the Supabase server, preventing unauthorized access to sensitive information.
2. **Access Control:** Define strict access controls and permissions for your database. Limit the privileges of each user to the minimum necessary for their role. Supabase provides role-based access control (RBAC), allowing you to define roles with specific permissions.
3. **API Key Management:** Safeguard your Supabase API keys. Treat them as sensitive information and avoid exposing them in client-side code or public repositories. Use environment variables or other secure methods to manage and use API keys in your application.
4. **Firewall Rules:** Configure firewall rules to restrict database access to specific IP addresses or ranges. This helps prevent unauthorized access to your Supabase database from external sources.
5. **Data Encryption:** Enable encryption at rest for your Supabase database to protect your data even when it's stored on disk. Supabase supports automatic encryption for data at rest.
6. **Regular Backups:** Implement regular backup procedures for your Supabase database. This ensures that you can recover your data in case of accidental deletion, corruption, or other unforeseen events.
7. **Secure File Uploads:** If your application allows file uploads, ensure that you implement proper security measures to prevent malicious file uploads. Validate file types, size, and use secure storage mechanisms.
8. **Authentication Best Practices:** Implement strong authentication mechanisms for your application. Consider using multi-factor authentication (MFA) to add an extra layer of security.

## Security recommendations when Managing healthcare data (optional)

You can use Supabase to store and process Protected Health Information (PHI). More details can be found at: [Shared Responsibility Model](https://supabase.com/docs/guides/platform/shared-responsibility-model)

|Supabase Recommendations| Set Correctly - Yes| Set Correctly - No| N/A |
|---------------|--------------------|-------------------|-----|
| Signing a Business Associate Agreement (BAA) with Supabase. Submit a HIPAA add-on request to get started. |   |   |   |
|Enabling Point in Time Recovery. |   |   |   |
|Turning on SSL Enforcement. |   |   |   |
|Enabling Network Restrictions. |   |   |   |
|Disabling Supabase AI editor in our dashboard. |   |   |   |
|Complying with encryption requirements in the HIPAA Security Rule. Data is encrypted at rest and in transit by Supabase. You can consider encrypting the data at your application layer. |   |   |   |
|Not using Edge functions to process PHI. |   |   |   |
|Not storing PHI in public Storage buckets. |   |   |   |

## References

\[1\] Supabase, "Security at Supabase," <em>Supabase</em>, 2023. https://supabase.com/security

\[2\] Supabase, "Shared Responsibility Model | Supabase Docs," <em>Supabase</em>, 2023. https://supabase.com/docs/guides/platform/shared-responsibility-model

###### _Emily Merchant, 8th December, 2023_