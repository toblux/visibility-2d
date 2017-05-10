debug:
	lein cljsbuild once debug

release:
	lein cljsbuild once release

figwheel:
	lein figwheel

clean:
	lein clean

demo: release
	cp resources/public/index.html docs/index.html
	cp resources/public/css/main.css docs/css/main.css
	cp resources/public/js/visibility-2d.js docs/js/visibility-2d.js

.PHONY: debug release figwheel clean demo
