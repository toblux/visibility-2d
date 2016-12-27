debug:
	lein cljsbuild once debug

release:
	lein cljsbuild once release

figwheel:
	lein figwheel

clean:
	-rm figwheel_server.log
	lein clean

.PHONY: debug release figwheel clean
