.PHONY: repl run test outdated pom uberjar jar clean

PLATFORM = $(shell uname -s)
ifeq ($(PLATFORM), Darwin)
	GRAAL_EXTRA_OPTION :=
else
	GRAAL_EXTRA_OPTION := "--static"
endif

repl:
	iced repl -A:dev

run:
	clj -m colf.core

test:
	clojure -R:dev -A:test

lint:
	cljstyle check
	clj-kondo --lint src:test

outdated:
	clojure -A:outdated

pom:
	clojure -Spom

uberjar: pom
	clojure -A:depstar -m hf.depstar.uberjar target/colf-standalone.jar -C -m colf.core

jar: pom
	clojure -A:depstar -m hf.depstar.jar target/colf.jar

native-image: uberjar
	$(GRAALVM_HOME)/bin/native-image \
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
		$(GRAAL_EXTRA_OPTION) \
		"-J-Xmx3g"

clean:
	rm -rf target
