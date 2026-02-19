# Key Material Creation Journal (JKS)

This journal documents, step-by-step, the exact commands used to create the demo certificates, keystores, and truststores in JKS format. All artifacts are stored in the `certs/` directory. Passwords are intentionally simple for an educational demo.

## Conventions

- Store password: `changeit`
- Key password: `changeit`
- Store type: JKS
- Subject values are local/demo values and do not represent real identities.

## 1) Create a local demo CA

Generate a self-signed CA keypair. The `bc=ca:true` and `keyCertSign,cRLSign` extensions mark this certificate as a certificate authority.

```
keytool -genkeypair -alias ca -keyalg RSA -keysize 2048 \
  -dname "CN=mtls-demo-ca, OU=Dev, O=Playground, L=Local, ST=Local, C=US" \
  -validity 3650 -keystore certs/ca.jks -storetype JKS \
  -storepass changeit -keypass changeit \
  -ext bc=ca:true -ext keyUsage=keyCertSign,cRLSign
```

Export the CA certificate so it can be imported into truststores and keystores as part of the chain.

```
keytool -exportcert -alias ca -keystore certs/ca.jks -storepass changeit \
  -rfc -file certs/ca.crt
```

## 2) Create and sign the server certificate

Generate the server keypair with a Subject Alternative Name for `localhost` and `127.0.0.1` so HTTPS clients can validate the hostname.

```
keytool -genkeypair -alias server -keyalg RSA -keysize 2048 \
  -dname "CN=localhost, OU=Dev, O=Playground, L=Local, ST=Local, C=US" \
  -validity 825 -keystore certs/server.jks -storetype JKS \
  -storepass changeit -keypass changeit \
  -ext SAN=dns:localhost,ip:127.0.0.1
```

Create a CSR (certificate signing request) for the server key.

```
keytool -certreq -alias server -keystore certs/server.jks -storepass changeit \
  -file certs/server.csr
```

Sign the CSR with the CA. The extensions declare the server's intended use as TLS server authentication.

```
keytool -gencert -alias ca -keystore certs/ca.jks -storepass changeit \
  -infile certs/server.csr -outfile certs/server.crt -rfc -validity 825 \
  -ext KU=digitalSignature,keyEncipherment -ext EKU=serverAuth \
  -ext SAN=dns:localhost,ip:127.0.0.1
```

Import the CA certificate into the server keystore (chain anchor), then import the signed server certificate to replace the self-signed placeholder.

```
keytool -importcert -alias ca -keystore certs/server.jks -storepass changeit \
  -noprompt -file certs/ca.crt

keytool -importcert -alias server -keystore certs/server.jks -storepass changeit \
  -file certs/server.crt
```

## 3) Create and sign the client certificate

Generate the client keypair.

```
keytool -genkeypair -alias client -keyalg RSA -keysize 2048 \
  -dname "CN=mtls-client, OU=Dev, O=Playground, L=Local, ST=Local, C=US" \
  -validity 825 -keystore certs/client.jks -storetype JKS \
  -storepass changeit -keypass changeit
```

Create a CSR for the client key.

```
keytool -certreq -alias client -keystore certs/client.jks -storepass changeit \
  -file certs/client.csr
```

Sign the CSR with the CA. The extensions declare the client's intended use as TLS client authentication.

```
keytool -gencert -alias ca -keystore certs/ca.jks -storepass changeit \
  -infile certs/client.csr -outfile certs/client.crt -rfc -validity 825 \
  -ext KU=digitalSignature,keyEncipherment -ext EKU=clientAuth
```

Import the CA certificate into the client keystore, then import the signed client certificate to replace the self-signed placeholder.

```
keytool -importcert -alias ca -keystore certs/client.jks -storepass changeit \
  -noprompt -file certs/ca.crt

keytool -importcert -alias client -keystore certs/client.jks -storepass changeit \
  -file certs/client.crt
```

## 4) Create truststores

The server truststore contains the CA used to sign client certificates (so it will accept only clients signed by this CA).

```
keytool -importcert -alias ca -keystore certs/server-trust.jks -storetype JKS \
  -storepass changeit -noprompt -file certs/ca.crt
```

The client truststore contains the CA used to sign the server certificate (so it will accept only that server identity).

```
keytool -importcert -alias ca -keystore certs/client-trust.jks -storetype JKS \
  -storepass changeit -noprompt -file certs/ca.crt
```

## 5) Create an invalid client credential set

This keypair is self-signed and is not trusted by the server truststore. Using it will produce a TLS handshake failure.

```
keytool -genkeypair -alias client-bad -keyalg RSA -keysize 2048 \
  -dname "CN=mtls-client-bad, OU=Dev, O=Playground, L=Local, ST=Local, C=US" \
  -validity 825 -keystore certs/client-bad.jks -storetype JKS \
  -storepass changeit -keypass changeit
```
