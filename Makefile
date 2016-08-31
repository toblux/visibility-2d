debug:
	lein cljsbuild once debug

release:
	lein cljsbuild once release

clean:
	lein clean

.PHONY: debug release clean
