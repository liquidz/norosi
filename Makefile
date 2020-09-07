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
	clojure -m norosi.core

test:
	clojure -R:dev -A:test

lint:
	cljstyle check
	clj-kondo --lint src:test

outdated:
	clojure -A:outdated

pom:
	clojure -X:deps mvn-pom

uberjar: pom
	clojure -A:depstar -m hf.depstar.uberjar target/norosi-standalone.jar -C -m norosi.core

jar: pom
	clojure -A:depstar -m hf.depstar.jar target/norosi.jar

native-image: uberjar
	bash script/native-image.sh

clean:
	rm -rf target
