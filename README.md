# mTLS Playground (JBang + Quarkus)

This playground shows a working mTLS handshake, then a predictable failure by switching client credentials.

## Run

Start the server:

```
jbang --fresh -Dquarkus.config.locations=server-jks.properties QServer.java
```

Run the client (success):

```
jbang --fresh -Dquarkus.config.locations=client-jks.properties QClient.java --message "hello"
```

Run the client (failure):

```
jbang --fresh -Dquarkus.config.locations=client-bad-jks.properties QClient.java --message "hello"

Note: JBang caches the Quarkus augmentation step. If you edit properties or certificates, rerun with `--fresh` to avoid using stale config.
```

## Files

- Properties: `server-jks.properties`, `client-jks.properties`, `client-bad-jks.properties`
- Key material: `certs/` (see the JKS journal for the exact creation steps)

## Expected behavior

- Success prints: `message received`
- Failure ends with an SSL/TLS handshake error because the server does not trust the client certificate in `client-bad.jks`
