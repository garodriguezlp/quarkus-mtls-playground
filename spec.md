## mTLS Playground Specification

### Overview

Create an educational playground that demonstrates mutual TLS (mTLS) using Quarkus in a JBang + Java 17 + Quarkus + Picocli context. The playground uses the existing [QClient.java](QClient.java) and [QServer.java](QServer.java) as the base. The goal is to clearly show a successful mTLS handshake, then demonstrate failure modes by changing client certificates/keys.

This document is a specification only. It defines behavior, configuration, assets, and acceptance criteria. No implementation or code is included.

### Goals
- Provide a minimal, clear, and repeatable mTLS example with a Quarkus server and a Quarkus client.
- Use declarative REST clients on the client side.
- Expose a simple server endpoint to test mTLS and a Picocli-based client command that calls it.
- Provide keystores/truststores for both sides, plus alternate credentials to demonstrate failure.
- Leverage JBang and Quarkus integration via properties files for configuration.

### Non-Goals
- Production-grade TLS hardening or PKI lifecycle management.
- Complex API surface or multiple endpoints.
- Custom certificate authorities beyond the minimum required for the demo.

### Inputs and Base Files
- Base entry points: [QClient.java](QClient.java) and [QServer.java](QServer.java).
- Configuration files: client and server properties files (names defined below).
- Key material: keystores and truststores for both client and server, plus alternate credentials to force failures.

### Functional Requirements
1. **Server API**
	- Provide a single HTTPS endpoint that accepts a POST request with a message.
	- Response payload must be JSON: `{"echo":"message received"}`.
	- Endpoint requires client certificate authentication (mTLS).

2. **Client Command**
	- Provide a CLI command that sends a message to the server and prints the response.
	- The message must be configurable via a Picocli option (for example: `--message`).
	- Use a declarative REST client for the request.

3. **mTLS Configuration**
	- Both client and server must be configured for mTLS using Quarkus TLS configuration.
	- Server must present a certificate trusted by the client.
	- Client must present a certificate trusted by the server.

4. **Failure Scenarios**
	- Provide a second client credential set that fails mTLS when used.
	- Failure can be demonstrated via either:
	  - a separate client keystore not trusted by the server, or
	  - multiple keys in the client keystore with at least one not trusted by the server.
	- Switching the client to the failing credential set must predictably fail with an SSL/TLS handshake error.

### Configuration Requirements
1. **Properties Files**
	- Create the following properties files:
	  - Client: `client.properties`
	  - Server: `server.properties`
	- These files must include all Quarkus TLS and REST client configuration needed for mTLS.
	- Configuration must be compatible with JBang + Quarkus property loading.

2. **Declarative REST Client**
	- The client must use Quarkus declarative REST client capabilities.
	- The client properties must include base URL and TLS settings for the REST client.

### Keystore and Truststore Requirements
1. **Artifacts to Provide** (use the terminal to create these using keytool or openssl and leave a journal of how they were created, very elaborated)
	- Server keystore containing the server certificate and private key.
	- Server truststore containing the CA or client certs required to validate client certs.
	- Client keystore containing the client certificate and private key.
	- Client truststore containing the CA or server certs required to validate server certs.
	- Additional client credential set to force a failed handshake.

2. **Naming and Locations**
	- The spec does not mandate exact filenames or paths, but they must be referenced clearly in properties files.

3. **Credential Switching**
	- The client must be able to switch between the valid and invalid credentials using configuration only.
	- The server configuration must remain unchanged during the failure demonstration.

### Operational Behavior
1. **Successful Flow**
	- Start server with server properties and valid server keystore/truststore.
	- Run client with valid client keystore/truststore.
	- Client sends message, server responds with `{"echo":"message received"}`.
	- Client prints the response to stdout.

2. **Failure Flow**
	- Run client with alternate invalid credentials.
	- Client fails to connect due to mTLS handshake failure.
	- Error should be clearly attributable to TLS authentication (no silent failures).

### Acceptance Criteria
- The playground can be configured using only properties files and supplied key material.
- The server only accepts connections from clients presenting trusted certificates.
- The client only trusts the server certificate configured in its truststore.
- The client uses a declarative REST client and a Picocli option to define the message.
- Switching client credentials to the invalid set consistently fails.

### Documentation Expectations
- The spec and resulting playground must be clear enough for a reader to understand:
  - how mTLS works in this example,
  - which files to configure and where,
  - how to run the success and failure scenarios.

### Assumptions
- Java 17 is available.
- JBang is available for running the client and server.
- Quarkus configuration is applied via properties files compatible with JBang.

### Risks and Considerations
- Misconfigured truststores or keystores may produce errors that are non-obvious; configuration must be explicit and documented.
- Differences in certificate formats (JKS vs PKCS12) must be consistent across client/server and properties.

### Glossary
- **mTLS**: Mutual TLS, where both client and server authenticate each other using certificates.
- **Keystore**: Holds a certificate and private key for presenting identity.
- **Truststore**: Holds certificates used to trust peers.