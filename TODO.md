# TODO

- How to improve network latency in case of HTTP 2.
- How to identify if a block/chunked is dropped during TCP transmission.
- OpenTelemetry


TLS Handshake Cost

TLS can easily cost:

100–300ms on mobile

Use:

HTTP/2
connection reuse
TLS session resumption


gzip versus brotli
- Testing
    - Performance testing with tools like Android Profiler, Benchmark library, etc.
    - Testing private classes with reflection or other techniques.
    - Testing with configuration cache enabled.

- perform build analyser.
- gradle scan?
- CI related to build time etc?
    - e.g. push build scan plugin to CI and monitor build time, cache hit/miss, etc.

- Custom DI using annotation processing and inject.
- caching library.
- Moshi usage: Gson versus moshi? Any advantages of moshi over gson?
- datadog
- senetry
- New kotlin features (AI suggestions):
    - context receivers
    - value classes
    - sealed interfaces
    - type inference improvements
    - contracts
    - compiler plugins (ksp, compiler reports, etc.)
    - multiplatform features (expect/actual, etc.)
    - coroutines improvements (structured concurrency, etc.)
    - kotlinx libraries (serialization, datetime, etc.)

    
Tier 3 — Useful for Maturity
7. Binary compatibility validator for api/ modules
   Use kotlinx-binary-compatibility-validator on each feature/*/api module. It generates .api dump files and fails CI if the public API surface changes unintentionally — important since 11 features expose public contracts.

9. Release workflow (tag-triggered)
   A separate release.yml workflow triggered by v* tags that builds a signed release APK (secrets for keystore), creates a GitHub Release, and uploads the APK. For a playground, this is nice-to-have.

- Analytics SDK.
    - Print all the events trigger so far
    - cover flow for lazy init and event pub sub.
- Implement effective logging, monitoring and alerting solutions for applications using tools like Prometheus, Grafana, etc.
- Unit test with private class.
- configuration cache.
- Jetcaster project for manual dependency
- Checkout kmm projects and bring the idea
- Compose testing library
- Compose Screenshot Testing Plugin @ Skydrove book
- You can use the compiler reports to determine what stability is being inferred about your classes.
- Detekt for entities


Proven experience with Android camera APIs (Camera2, CameraX).
Experience with image processing libraries (e.g., OpenCV, Glide).
Familiarity with multimedia frameworks (e.g., FFmpeg).
MANDATORY – Hands on Experience with Qualcomm CAMX stack. Feature implementation and debugging.
Hands on experience on running ML models in camera pipeline on DSP, GPU etc
Understanding of camera tuning concepts preferred.

Check github issues for this repo.



K2 Compiler
r8 configuration analyser
    https://www.youtube.com/watch?v=fOXJR5qLq54 
    r8 skill
Android APA Android Performance Analyzer 