# Firebase Security Checklist

| Recommendation | Set Correctly - Yes | Set Correctly - No | N/A |
| --------------- | -------------------- | ------------------- | --- |
| **Avoid abusive traffic** |
| - Set up monitoring and alerting for backend services | | | |
| - Enable App Check | | | |
| - Configure your Cloud Functions to scale for normal traffic | | | |
| - Set up alerting to be notified when the limits are nearly reached | | | |
| - Prevent self-DOSes: test functions locally with the emulators | | | |
| - Where real-time responsiveness is less important, structure functions defensively | | | |
| **Understand API keys** |
| - API keys for Firebase services are not secret | | | |
| - Set up API key scoping | | | |
| - Keep FCM server keys secret | | | |
| - Keep service account keys secret | | | |
| **Security rules** |
| - Initialize rules in production or locked mode | | | |
| - Security rules are a schema; add rules when you add documents | | | |
| - Unit test security rules with the Emulator Suite; add it to CI | | | |
| **Authentication** |
| - Custom authentication: mint JWTs from a trusted (server-side) environment | | | |
| - Managed authentication: OAuth 2.0 providers are the most secure | | | |
| - Email-password authentication: set a tight quota for the sign-in endpoint to prevent brute force attacks | | | |
| - Email-password authentication: Enable email enumeration protection | | | |
| - Upgrade to Cloud Identity Platform for multi-factor authentication | | | |
| **Anonymous authentication** |
| - Only use anonymous authentication for warm onboarding | | | |
| - Convert users to another sign-in method if they'll want the data when they lose their phone | | | |
| - Use security rules that require users to have converted to a sign-in provider or have verified their email | | | |
| **Environment management** |
| - Set up development and staging projects | | | |
| - Limit team access to production data | | | |
| **Library management** |
| - Watch out for library misspellings or new maintainers | | | |
| - Don't update libraries without understanding the changes | | | |
| - Install watchdog libraries as dev or test dependencies | | | |
| - Set up monitoring for Functions; check it after library updates | | | |
| **Cloud Function safety** |
| - Never put sensitive information in a Cloud Function's environment variables | | | |
| - Encrypt sensitive information | | | |
| - Simple functions are safer; if you need complexity, consider Cloud Run | | | |

## References
\[1\] Google, "Firebase security checklist," <em>Firebase</em>, 2023. https://firebase.google.com/support/guides/security-checklist


###### _Emily Merchant, 8th December, 2023_