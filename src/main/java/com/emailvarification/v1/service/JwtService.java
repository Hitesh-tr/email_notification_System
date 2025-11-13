package com.emailvarification.v1.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extractClaims, UserDetails userDetails){
        return buildToken(extractClaims, userDetails, jwtExpiration);
    }

    public String buildToken(Map<String, Object> extractClaims,
                             UserDetails userDetails, long jwtExpiration){
        return Jwts
            .builder() //Create new JWT
            .setClaims(extractClaims) //add Custom data
            .setSubject(userDetails.getUsername()) // Set Username
            .setIssuedAt(new Date(System.currentTimeMillis())) //set Creation time
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // sets expiry
            .signWith(getSignInKey(), SignatureAlgorithm.HS256) //Sign token with secret key
            .compact(); // joins all header.payload.signature, final token
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token){
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /*
        Gets
     */

    public String extractUsername(String token){
        /*
        That means the second argument (claims -> claims.getSubject())
        must be something that implements this functional interface:
         */
        return extractClaim(token, Claims::getSubject);

        // return extractClaim(token, claims -> claims.getSubject());
        /*
        Java infers:
        claims is the input (T t → Claims claims)
        claims.getSubject() is the return value (R apply(T) → returns String)
        So this lambda means:
        "an implementation of the apply() method inside the Function<Claims, String> interface"
         */
    }

    /*
    Claims::getSubject — what it really is

    It isn’t storing the subject value yet.

    It’s storing a function reference — a recipe that says:

    “When you give me a Claims object, I’ll call getSubject() on it and return the result.”

    So claimResolver is not holding the subject, it’s holding the instructions to extract the subject.
     */
    public <T> T extractClaim(String token,
                              Function<Claims, T> claimResolver //“I need a Function<Claims, T> — that is, something that takes a Claims and returns a T.”
    ){
        final Claims claims = extractAllClaims(token);

        return claimResolver.apply(claims);

        /*
        claimResolver stores a function (a recipe) that knows how to get the subject from the claims.
        Later, when .apply(claims) is called, that function is executed — it reads the actual subject
        from the Claims object and returns it.

        token  ──▶ extractAllClaims(token) ──▶ Claims{ sub="hitesh", role="ADMIN" }
                           │
                           ▼
        claimResolver = (claims -> claims.getSubject())
                           │
                           ▼
        claimResolver.apply(claims)
                           │
                           ▼
        "hitesh"

         */
    }

    public Claims extractAllClaims(String token){
        return Jwts
            .parserBuilder() // create a parse Builder, to revert back or reading jwt
            .setSigningKey(getSignInKey()) // provide secretkey for varification and checks with it
            .build() //Finalizes the builder configuration and returns a JwtParser instance.
            .parseClaimsJws(token)//Splits the token into header, payload, and signature
            .getBody(); //Returns the payload part of the JWT
    }

    private Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
