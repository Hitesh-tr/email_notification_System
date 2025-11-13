"# email_notification_System" 

config package:
by default we will be having basic http authentication.

so we need to over-ride it for
1. it needs to perform the authentication by find user from our database.
2. generate a JWT token when authentication is successful.