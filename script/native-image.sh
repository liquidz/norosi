#!/bin/bash

if [ "$(uname -s)" = 'Darwin' ]; then
  GRAAL_EXTRA_OPTION=''
else
  GRAAL_EXTRA_OPTION='--static'
fi

if [ "${GRAALVM_HOME}" = '' ]; then
  NATIVE_IMAGE='native-image'
else
  NATIVE_IMAGE="${GRAALVM_HOME}/bin/native-image"
fi

${NATIVE_IMAGE} \
  -jar target/colf-standalone.jar \
  -H:Name=target/colf \
  -H:+ReportExceptionStackTraces \
  -J-Dclojure.spec.skip-macros=true \
  -J-Dclojure.compiler.direct-linking=true \
  --initialize-at-build-time  \
  --report-unsupported-elements-at-runtime \
  -H:Log=registerResource: \
  --verbose \
  --no-fallback \
  --no-server \
  ${GRAAL_EXTRA_OPTION} \
  "-J-Xmx3g"
