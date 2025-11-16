"# email_notification_System" 

---

config package:
by default we will be having basic http authentication.

so we need to over-ride it for
1. it needs to perform the authentication by find user from our database.
2. generate a JWT token when authentication is successful.

---

JwtAuthenticationFilter.java
* This filter runs once per HTTP request (Spring ensures it) and checks if a JWT token is present and valid.
* If valid → Spring treats the user as authenticated.
  If not → the request passes through as unauthenticated.

Client → [JwtAuthenticationFilter] → Controller
