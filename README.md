# mTLS Playground (JBang + Quarkus)

This playground shows a working mTLS handshake, then a predictable failure by switching client credentials.

## Run

Start the server:

```
jbang --fresh QServer.java
```

Run the client (success):

```
jbang --fresh QClient.java --message "hello"
```

### Switching to bad credentials

To test with bad credentials, edit the Java file to reference the bad config:

**For client:**
Change `//FILES application-client.properties` to `//FILES application-client-bad.properties` in `QClient.java`, then:
```
jbang --fresh QClient.java --message "hello"
```

This should fail with an SSL/TLS handshake error.

**Note:**
- Configuration files are embedded using `//FILES` and automatically loaded via Quarkus profiles.
- To switch between good/bad credentials, edit the `//FILES` reference in the Java file.
- Rerun with `--fresh` to clear JBang's cache and reload the configuration.

## Files

- Properties: `server-jks.properties`, `client-jks.properties`, `client-bad-jks.properties`
- Key material: `certs/` (see the JKS journal for the exact creation steps)

## Expected behavior

- Success prints: `message received`
- Failure ends with an SSL/TLS handshake error because the server does not trust the client certificate in `client-bad.jks`
